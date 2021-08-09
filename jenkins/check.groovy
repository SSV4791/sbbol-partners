import ru.sbrf.ufs.pipeline.Const

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
        GIT_PROJECT = 'CIBPPRB'
        GIT_REPOSITORY = 'sbbol-partners'
        PR_CHECK_LABEL = 'pr_check'
        pullRequest = null
        NEXUS_CREDS_ID = 'DS_CAB-SA-CI000825'
        NEXUS_CREDS = credentials("${NEXUS_CREDS_ID}")
        SONAR_TOKEN = credentials('sonar-token-partners')
        SONAR_PROJECT = 'ru.sberbank.pprb.sbbol.partners:partners'
        latestCommitHash = ''
    }
    stages {
        stage('Init') {
            steps {
                script {
                    pullRequest = bitbucket.getPullRequest(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId.toInteger())
                    bitbucket.setJobPullRequestLink(pullRequest)
                    bitbucket.setJenkinsLabelInfo(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL)
                    bitbucket.updateBitbucketHistoryBuild(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, stage_name, "running")
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
        stage('Compile and Check') {
            steps {
                script {
                    docker.withRegistry(Const.OPENSHIFT_REGISTRY, NEXUS_CREDS_ID) {
                        sh 'docker run --rm ' +
                            '-v "$(pwd)":/build ' +
                            '-v "$(pwd)"/../.m2:/root/.m2 ' +
                            '-w /build ' +
                            '-e "M2_HOME=/root/.m2" ' +
                            '-e "MVNW_REPOURL=http://sbtatlas.sigma.sbrf.ru/nexus/content/groups/public/" ' +
                            '-e "MVNW_VERBOSE=true" ' +
                            "-e \"REPO_USER=${NEXUS_CREDS_USR}\" " +
                            "-e \"REPO_PASSWORD=${NEXUS_CREDS_PSW}\" " +
                            'registry.sigma.sbrf.ru/ci00149046/ci00405008_sbbolufs/openjdk:11-with-certs ' +
                            './mvnw clean install sonar:sonar' +
                            " -P sonar" +
                            " -e" +
                            " -Dsonar.host.url=https://sbt-sonarqube.sigma.sbrf.ru/" +
                            " -Dsonar.login=${SONAR_TOKEN}" +
                            " -Dsonar.projectKey=${SONAR_PROJECT}" +
                            " -Dsonar.pullrequest.key=${params.pullRequestId}" +
                            " -Dsonar.pullrequest.branch=${pullRequest.fromRef.displayId}" +
                            " -Dsonar.pullrequest.base=${pullRequest.toRef.displayId}" +
                            " -s /build/jenkins/settings.xml"
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                bitbucket.setJenkinsLabelStatus(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, true)
                bitbucket.updateBitbucketHistoryBuild(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "success", "successful")
            }
        }
        failure {
            script {
                bitbucket.updateBitbucketHistoryBuild(NEXUS_CREDS_ID, GIT_PROJECT, GIT_REPOSITORY, params.pullRequestId, PR_CHECK_LABEL, "failure", "failed")
            }
        }
        cleanup {
            script {
                cleanWs()
            }
        }
    }
}
