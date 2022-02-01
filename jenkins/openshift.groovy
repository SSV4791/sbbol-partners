import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _

def credential = secman.makeCredMap('DS_CAB-SA-CI000825')
def bitbucketCredential = Const.BITBUCKET_DBO_KEY_SECMAN
def pullRequest = null
def latestCommitHash = ""

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
        PR_CHECK_LABEL = 'openshift'
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
                    latestCommitHash = git.checkoutRef bitbucketCredential, GIT_PROJECT, GIT_REPOSITORY, "${pullRequest.fromRef.displayId}:${pullRequest.fromRef.displayId} ${pullRequest.toRef.displayId}:${pullRequest.toRef.displayId}"
                    sh "git merge ${pullRequest.toRef.displayId}"
                }
            }
        }

        stage('Check OS Templates') {
            steps {
                script {
                    def ymlPaths = findFiles(glob: "**/openshift/**/*.yml.j2")
                        .collect { "${WORKSPACE}/${it.path}" }
                    log.info(ymlPaths)

                    def paramsPath = findFiles(glob: "**/jenkins/resources/openshift/values/*.yml")
                        .collect { "${WORKSPACE}/${it.path}" }
                    log.info(paramsPath)

                    def resourcesPath = "${WORKSPACE}/jenkins/resources/openshift" as String
                    withEnv(["ANSIBLE_CONFIG=${resourcesPath}/ansible.cfg"]) {
                        ansible.templates(
                            ymlPaths,
                            "${resourcesPath}",
                            paramsPath
                        )
                        sh "zip -q ansible_playbooks.zip ./jenkins/resources/openshift/*"
                        yamllintCheck("${resourcesPath}")
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
                archiveArtifacts artifacts: "ansible_playbooks.zip", allowEmptyArchive: true
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
