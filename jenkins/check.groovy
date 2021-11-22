import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _

def ufsCredential = 'DS_CAB-SA-CI000825'

def labels = [
    build    : [
        entries: ['src/', 'build.gradle.kts', 'gradle.properties', 'settings.gradle.kts', 'jenkins/build.groovy']
    ],
    openshift: [
        entries: ['openshift/', 'jenkins/resources/openshift/', 'jenkins/openshift.groovy']
    ],
    liquibase: [
        entries: ['src/main/resources', 'jenkins/liquibase.groovy']
    ]
]

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
        PR_CHECK_LABEL = 'pr_check'
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
                    def defaultLabels = [PR_CHECK_LABEL]
                    def changedFiles = git.getPrDiffFiles(GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger(), [], ufsCredential)
                    def checkingPaths = labels.collect { it.value.entries }.flatten().unique()
                    def intersectEntries = fileUtils.intersect(checkingPaths, changedFiles)
                    additionalLabels = labels.findAll { it.value.entries.any { entry -> intersectEntries.contains(entry) } }.keySet()

                    bitbucket.setLabels(ufsCredential, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger(), defaultLabels + additionalLabels as Set)
                }
            }
        }
        stage('Trigger checks') {
            steps {
                script {
                    additionalLabels.each {
                        stage("Trigger ${it}") {
                            build job: "./${it}", parameters: [
                                string(name: 'pullRequestId', value: params.pullRequestId)
                            ],
                                wait: false
                        }
                    }
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
