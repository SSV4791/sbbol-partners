/**
 * Пайплайн PR Check
 */

@Library(['ufs-jobs@master']) _

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
        BITBUCKET_CREDENTIALS_ID = 'sbbol-bitbucket'
        NEXUS_CREDENTIALS_ID = 'SBBOL-build'
        GIT_PROJECT = 'CIBPPRB'
        GIT_REPOSITORY = 'sbbol-partners'
        PR_CHECK_LABEL = 'pr_check'
        pullRequest = null
    }
    stages {
        stage('Init') {
            steps {
                script {
                    pullRequest = bitbucket.getPullRequest(BITBUCKET_CREDENTIALS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    bitbucket.setJobPullRequestLink(pullRequest)
                    bitbucket.setBuildStatus(BITBUCKET_CREDENTIALS_ID, 'INPROGRESS', PR_CHECK_LABEL, pullRequest.fromRef.latestCommit)
                    bitbucket.setJenkinsLabelInfo(
                            BITBUCKET_CREDENTIALS_ID,
                            GIT_PROJECT,
                            GIT_REPOSITORY,
                            params.pullRequestId,
                            PR_CHECK_LABEL)
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
                withCredentials([usernamePassword(
                        credentialsId: NEXUS_CREDENTIALS_ID,
                        usernameVariable: 'USERNAME',
                        passwordVariable: 'PASSWORD'
                )]) {
                    sh 'chmod +x mvnw'
                    sh 'docker run --rm ' +
                            '-v "$(pwd)":/build ' +
                            '-v "$(pwd)"/../.m2:/root/.m2 ' +
                            '-w /build ' +
                            '-e "M2_HOME=/root/.m2" ' +
                            '-e "MVNW_REPOURL=http://sbtatlas.sigma.sbrf.ru/nexus/content/groups/public/" ' +
                            '-e "MVNW_VERBOSE=true" ' +
                            "-e \"REPO_USER=${USERNAME}\" " +
                            "-e \"REPO_PASSWORD=${PASSWORD}\" " +
                            'sbtatlas.sigma.sbrf.ru:5000/openjdk:11 ' +
                            './mvnw clean install -DskipTests -s /build/jenkins/settings.xml'
                }
            }
        }
    }
    post {
        success {
            script {
                bitbucket.setBuildStatus(BITBUCKET_CREDENTIALS_ID, 'SUCCESSFUL', PR_CHECK_LABEL, pullRequest.fromRef.latestCommit)
                bitbucket.setJenkinsLabelStatus(
                        BITBUCKET_CREDENTIALS_ID,
                        GIT_PROJECT,
                        GIT_REPOSITORY,
                        params.pullRequestId,
                        PR_CHECK_LABEL,
                        true)
            }
        }
        failure {
            script {
                bitbucket.setBuildStatus(BITBUCKET_CREDENTIALS_ID, 'FAILED', PR_CHECK_LABEL, pullRequest.fromRef.latestCommit)
            }
        }
        cleanup {
            script {
                cleanWs()
            }
        }
    }
}
