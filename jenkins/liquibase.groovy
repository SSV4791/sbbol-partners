import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

def pullRequest = null
def latestCommitHash = ""
def ufsCredential = 'DS_CAB-SA-CI000825'
def network = UUID.randomUUID()

pipeline {
    agent {
        label 'ufs-pr-check'
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        string(name: 'pullRequestId', description: 'ID пулл-реквеста')
    }

    environment {
        PR_CHECK_LABEL = 'liquibase'
        POSTGRES_DOCKER_IMAGE = 'registry.sigma.sbrf.ru/ci00149046/ci00405008_sbbolufs/postgres:13-alpine'
        NETWORK_ALIAS = 'postgres'
        POSTGRES_DB_NAME = 'db'
        POSTGRES_DB_USER = 'user'
        POSTGRES_DB_PASSWORD = 'pass'
        LIQUIBASE_DOWNLOAD_URI = 'https://nexus.sigma.sbrf.ru/nexus/service/local/repositories/SBT_CI_distr_repo/content/SBBOL_UFS/liquibase/3.7.0-postgres/liquibase-3.7.0-postgres-bin.tar.gz'
        NEXUS_CREDS = credentials("${ufsCredential}")
    }

    stages {
        /**
         * Чтение переменных окружения из файла
         */
        stage('Read env properties') {
            steps {
                load "./jenkins/env.groovy"
            }
        }
        stage('Preparing job') {
            steps {
                script {
                    pullRequest = bitbucket.getPullRequest(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    setJobPullRequestLink(pullRequest)
                    bitbucket.setJenkinsLabelInfo(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL)
                    bitbucket.updateBitbucketHistoryBuild(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, "running")
                }
            }
        }

        stage('Prepare project') {
            steps {
                script {
                    latestCommitHash = git.checkoutRef 'bitbucket-dbo-key', GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId} ${pullRequest.toRef.displayId}:${pullRequest.toRef.displayId} "
                    sh "git merge ${pullRequest.toRef.displayId}"
                }
            }
        }

        stage('Create PG') {
            steps {
                script {
                    sh("docker network create ${network}")
                    new DockerRunBuilder(this)
                            .registry(Const.OPENSHIFT_REGISTRY, ufsCredential)
                            .env("POSTGRES_USER", "${POSTGRES_DB_USER}")
                            .env("POSTGRES_PASSWORD", "${POSTGRES_DB_PASSWORD}")
                            .env("POSTGRES_DB", "${POSTGRES_DB_NAME}")
                            .extra("--network=${network} --network-alias ${NETWORK_ALIAS}")
                            .detached()
                            .cpu(1)
                            .memory("1g")
                            .image(POSTGRES_DOCKER_IMAGE)
                            .run()
                }
            }
        }

        stage('Check sql script') {
            steps {
                script {
                    sh "curl -u ${NEXUS_CREDS_USR}:${NEXUS_CREDS_PSW} -kL ${LIQUIBASE_DOWNLOAD_URI} | tar -xz"

                    new DockerRunBuilder(this)
                            .registry(Const.OPENSHIFT_REGISTRY, ufsCredential)
                            .volume("${WORKSPACE}", "/build")
                            .extra("-w /build")
                            .extra("--network=${network}")
                            .cpu(1)
                            .memory("1g")
                            .image(BUILD_JAVA_DOCKER_IMAGE)
                            .cmd('sh liquibase ' +
                                    "--url=jdbc:postgresql://${NETWORK_ALIAS}/${POSTGRES_DB_NAME} " +
                                    "--username=${POSTGRES_DB_USER} " +
                                    "--password=${POSTGRES_DB_PASSWORD} " +
                                    "--changeLogFile=runner/src/main/resources/db/changelog/changelog.yaml " +
                                    "--defaultsFile=jenkins/resources/liquibase/liquibase.properties " +
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
                bitbucket.setJenkinsLabelStatus(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, true)
                bitbucket.updateBitbucketHistoryBuild(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "success", "successful")
            }
        }
        failure {
            script {
                bitbucket.updateBitbucketHistoryBuild(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "failure", "failed")
            }
        }
        cleanup {
            script {
                sh "docker stop \$(docker ps --filter \"network=${network}\") || true"
                sh "docker network rm ${network} || true"
                cleanWs()
            }
        }
    }
}
