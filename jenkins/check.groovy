import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

/**
 * Пайплайн пулл реквест чека
 */

@Library(['ufs-jobs@master']) _

def pullRequest = null
def ufsCredential = 'DS_CAB-SA-CI000825'

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
        NEXUS_CREDS = credentials("${ufsCredential}")
        SONAR_TOKEN = credentials('sonar-token-partners')
        PR_CHECK_LABEL = 'pr_check'
        pullRequest = null
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
                    git.checkoutRef 'bitbucket-dbo-key', GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId} ${pullRequest.toRef.displayId}:${pullRequest.toRef.displayId} "
                    sh "git merge ${pullRequest.toRef.displayId}"
                }
            }
        }
        stage('Compile and Check') {
            steps {
                script {
                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, ufsCredential)
                        .volume("${WORKSPACE}", "/build")
                        .extra("-w /build")
                        .cpu(2)
                        .memory("2g")
                        .image(BUILD_JAVA_DOCKER_IMAGE)
                        .cmd('./gradlew ' +
                            "-PnexusLogin=${NEXUS_CREDS_USR} " +
                            "-PnexusPassword=${NEXUS_CREDS_PSW} " +
                            "-Dsonar.login=${SONAR_TOKEN} " +
                            "-Dsonar.pullrequest.key=${params.pullRequestId} " +
                            "-Dsonar.pullrequest.branch=${pullRequest.fromRef.displayId} " +
                            "-Dsonar.pullrequest.base=${pullRequest.toRef.displayId} " +
                            "clean build sonarqube --parallel"
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
                cleanWs()
            }
        }
    }
}
