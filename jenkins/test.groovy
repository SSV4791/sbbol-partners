import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

String jobAllureServerUrl = ''
def pullRequest = null
def network = UUID.randomUUID()
def settings = [:]
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
        PR_CHECK_LABEL = 'test'
        NETWORK_ALIAS_MAIN = 'postgres_main'
        NETWORK_ALIAS_SI = 'postgres_si'
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
        stage('Init') {
            steps {
                script {
                    pullRequest = bitbucket.getPullRequest(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    setJobPullRequestLink(pullRequest)
                    bitbucket.setJenkinsLabelInfo(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL)
                    bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, 'running')
                }
            }
        }

        stage('Prepare project') {
            steps {
                script {
                    echo "$bitbucketSshCredential"
                    git.checkoutRef bitbucketSshCredential, GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.toRef.displayId} ${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId}"
                    sh "git merge ${pullRequest.fromRef.displayId}"
                    settings = readYaml(file: 'jenkins/settings.yml')
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
                        .extra("--network-alias ${NETWORK_ALIAS_MAIN}")
                        .detached()
                        .cpu(1)
                        .memory("1g")
                        .image(settings.docker.images.postgres)
                        .run()
                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, bitbucketCredential)
                        .env("POSTGRES_USER", "${POSTGRES_DB_USER}")
                        .env("POSTGRES_PASSWORD", "${POSTGRES_DB_PASSWORD}")
                        .env("POSTGRES_DB", "${POSTGRES_DB_NAME}")
                        .extra("--network=${network}")
                        .extra("--network-alias ${NETWORK_ALIAS_SI}")
                        .detached()
                        .cpu(1)
                        .memory("1g")
                        .image(settings.docker.images.postgres)
                        .run()
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    allureEe.run([
                        projectId   : settings.allure_project_id,
                        allureResult: ['build/allure-results'],
                        silent      : true
                    ]) { launch ->
                        jobAllureServerUrl = "${Const.ALLURE_ENTRYPOINT_URL}jobrun/${launch.jobRunId}"
                        bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, "running", jobAllureServerUrl)

                        vault.withUserPass([path: settings.credentials.nexus.path, userVar: 'NEXUS_USR', passVar: 'NEXUS_PWD']) {
                            vault.withSecretKey([path: settings.credentials.sonar.path, secretKeyVar: 'SONAR_TOKEN']) {
                                withEnv(["NEXUS_USR=${NEXUS_USR}", "NEXUS_PWD=${NEXUS_PWD}", "SONAR_TOKEN=${SONAR_TOKEN}"]) {
                                    new DockerRunBuilder(this)
                                        .registry(Const.OPENSHIFT_REGISTRY, bitbucketCredential)
                                        .volume("${WORKSPACE}", '/build')
                                        .extra('-w /build')
                                        .extra("--network=${network}")
                                        .cpu(settings.pr_check.test.cpu ?: 1)
                                        .memory(settings.pr_check.test.memory ?: '2g')
                                        .image(settings.docker.images.java)
                                        .cmd(format.interpolate(settings.pr_check.test.command, [pullRequest: pullRequest, launch: launch]))
                                        .run()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                bitbucket.setJenkinsLabelStatus(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, true)
                bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, 'success', 'successful', jobAllureServerUrl)
            }
        }
        failure {
            script {
                bitbucket.updateBitbucketHistoryBuild(bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, 'failure', 'failed', jobAllureServerUrl)
            }
        }
        cleanup {
            cleanWs()
        }
    }
}
