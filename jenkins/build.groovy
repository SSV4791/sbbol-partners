import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

def pullRequest = null
def credential = secman.makeCredMap('DS_CAB-SA-CI000825')
def sonarCredential = secman.makeCredMap('DS_CAB-SA-CI000825-sonar-token')
def bitbucketCredential = Const.BITBUCKET_DBO_KEY_SECMAN

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
        PR_CHECK_LABEL = 'build'
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
                    pullRequest = bitbucket.getPullRequest(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    setJobPullRequestLink(pullRequest)
                    bitbucket.setJenkinsLabelInfo(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL)
                    bitbucket.updateBitbucketHistoryBuild(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, "running")
                }
            }
        }

        stage('Prepare project') {
            steps {
                script {
                    git.checkoutRef bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId} ${pullRequest.toRef.displayId}:${pullRequest.toRef.displayId} "
                    sh "git merge ${pullRequest.toRef.displayId}"
                }
            }
        }

        stage('Build Java') {
            steps {
                script {
                    vault.withUserPass([path: credential.path, userVar: "NEXUS_USER", passVar: "NEXUS_PASSWORD"]) {
                        vault.withSecretKey([path: sonarCredential.path, secretKeyVar: "SONAR_TOKEN"]) {
                            new DockerRunBuilder(this)
                                .registry(Const.OPENSHIFT_REGISTRY, credential)
                                .volume("${WORKSPACE}", "/build")
                                .extra("-w /build")
                                .cpu(2)
                                .memory("2g")
                                .image(BUILD_JAVA_DOCKER_IMAGE)
                                .cmd('./gradlew ' +
                                    "-PnexusLogin=${NEXUS_USER} " +
                                    "-PnexusPassword=${NEXUS_PASSWORD} " +
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
        }
    }

    post {
        success {
            script {
                bitbucket.setJenkinsLabelStatus(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, true)
                bitbucket.updateBitbucketHistoryBuild(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "success", "successful")
            }
        }
        failure {
            script {
                bitbucket.updateBitbucketHistoryBuild(credential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "failure", "failed")
            }
        }
        cleanup {
            script {
                cleanWs()
            }
        }
    }
}
