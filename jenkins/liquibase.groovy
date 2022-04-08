import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

def pullRequest = null
def settings = [:]
def network = UUID.randomUUID()
def bitbucketCredential = null
def bitbucketSshCredential = null

pipeline {
    agent {
        label 'ufs-pr-check'
    }

    options {
        timestamps()
        skipDefaultCheckout()
    }

    parameters {
        string(name: 'pullRequestId', description: 'ID пулл-реквеста')
    }

    environment {
        PR_CHECK_LABEL = 'liquibase'
        NETWORK_ALIAS = 'postgres'
        POSTGRES_DB_NAME = 'db'
        POSTGRES_DB_USER = 'user'
        POSTGRES_DB_PASSWORD = 'pass'
    }

    stages {
        stage('Read jenkins folder configuration') {
            steps {
                script {
                    configFileProvider([configFile(fileId: 'common', variable: 'CONFIG')]) {
                        def config = readYaml(file: CONFIG)
                        config.each { k, v -> env."${k}" = v }
                        bitbucketCredential = secman.makeCredMapWithEnvs(BITBUCKET_REST_CREDENTIALS_ID)
                        bitbucketSshCredential = env.BITBUCKET_SSH_CREDENTIALS_ID ? secman.makeCredMapWithEnvs(env.BITBUCKET_SSH_CREDENTIALS_ID) : Const.BITBUCKET_DBO_KEY_SECMAN
                    }
                }
            }
        }
        stage('Preparing job') {
            steps {
                script {
                    pullRequest = bitbucket.getPullRequest(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    setJobPullRequestLink(pullRequest)
                    bitbucket.setJenkinsLabelInfo(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL)
                    bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, "running")
                }
            }
        }

        stage('Prepare project') {
            steps {
                script {
                    git.checkoutRef bitbucketSshCredential, GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.toRef.displayId} ${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId}"
                    sh "git merge ${pullRequest.fromRef.displayId}"
                    settings = readYaml(file: "jenkins/settings.yml")
                    settings.credentials = settings.credentials.collectEntries { key, value -> [key, secman.makeCredMapWithEnvs(value)] }
                }
            }
        }

        stage('Create PG') {
            steps {
                script {
                    sh("docker network create ${network}")
                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, bitbucketCredential)
                        .env("POSTGRES_USER", "${POSTGRES_DB_USER}")
                        .env("POSTGRES_PASSWORD", "${POSTGRES_DB_PASSWORD}")
                        .env("POSTGRES_DB", "${POSTGRES_DB_NAME}")
                        .extra("--network=${network}")
                        .extra("--network-alias ${NETWORK_ALIAS}")
                        .detached()
                        .cpu(1)
                        .memory("1g")
                        .image(settings.docker.images.postgres)
                        .run()
                }
            }
        }

        stage('Check sql script') {
            steps {
                script {
                    vault.withUserPass([path: settings.credentials.nexus.path, userVar: 'NEXUS_USR', passVar: 'NEXUS_PWD']) {
                        sh "curl -u '${NEXUS_USR}:${NEXUS_PWD}' -kL ${settings.pr_check.liquibase.executable.download_uri} | tar -xz"
                    }

                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, bitbucketCredential)
                        .volume("${WORKSPACE}", "/build")
                        .extra("-w /build")
                        .extra("--network=${network}")
                        .cpu(1)
                        .memory("1g")
                        .image(settings.docker.images.java)
                        .cmd('sh liquibase ' +
                            "--url=jdbc:postgresql://${NETWORK_ALIAS}/${POSTGRES_DB_NAME} " +
                            "--username=${POSTGRES_DB_USER} " +
                            "--password=${POSTGRES_DB_PASSWORD} " +
                            "--changeLogFile=${settings.pr_check.liquibase.changelog.path} " +
                            "--defaultsFile=${settings.pr_check.liquibase.defaults_file.path} " +
                            "--driver=org.postgresql.Driver " +
                            'update'
                        )
                        .run()
                }
            }
        }

    }

    post {
        success {
            script {
                bitbucket.setJenkinsLabelStatus(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, true)
                bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "success", "successful")
            }
        }
        failure {
            script {
                bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "failure", "failed")
            }
        }
        cleanup {
            script {
                sh "docker stop \$(docker ps -q --filter \"network=${network}\") || true"
                sh "docker network rm ${network} || true"
                cleanWs()
            }
        }
    }
}
