import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _

def pullRequest = null
def upstreamBranchName = ''
def credential = secman.makeCredMap('DS_CAB-SA-CI000825')
def bitbucketCredential = secman.makeCredMap('bitbucket-dbo-key') // Если используете централизованный кред, укажите Const.BITBUCKET_DBO_KEY_SECMAN

pipeline {
    agent {
        label 'ufs-upstream'
    }
    options {
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'sourcePullRequestId', description: 'ID пулл-реквеста')
        string(name: 'projectKey', description: 'ID проекта')
        string(name: 'repoSlug', description: 'ID репозитория')
        string(name: 'targetBranch', description: 'Target ветка пулл реквеста на перелитие')
        string(name: 'epicKey', description: 'Ключ epicLink\'ка для заведения задачи на перелитие')
    }

    stages {
        stage('Init') {
            steps {
                script {
                    currentBuild.getChangeSets().clear()
                    pullRequest = bitbucket.getPullRequest(credential, params.projectKey, params.repoSlug, params.sourcePullRequestId.toInteger())
                    setJobPullRequestLink(pullRequest)
                    if (pullRequest.state != 'MERGED') {
                        error("Только вмердженные ПРы можно переливать!")
                    }
                }
            }
        }
        stage('Upstream pull request') {
            steps {
                script {
                    def commits = bitbucket.getPullRequestCommits(credential, params.projectKey, params.repoSlug, params.sourcePullRequestId)
                    echo "Upstream commits:"
                    commits.each { echo "${it.id}:${it.message}" }
                    upstreamBranchName = params.sourcePullRequestId + "-upstream"
                    def commitHash = git.checkoutRef bitbucketCredential, params.projectKey, params.repoSlug, "${params.targetBranch}:${params.targetBranch} ${pullRequest.toRef.displayId}:${pullRequest.toRef.displayId}"
                    sh "git checkout -b ${upstreamBranchName} ${params.targetBranch}"
                    try {
                        sh "git cherry-pick ${commits.collect { it.id }.join(' ')} "
                        git.push bitbucketCredential, Const.BITBUCKET_SERVER_URL, params.projectKey, params.repoSlug, upstreamBranchName
                    } finally {
                        sh "git cherry-pick --abort || true"
                        // если черрипик свалился то надо вернуть в нормальное состояние ветку
                        //удаляем все созданные ветки что бы не мешали дальнейшей работе
                        sh "git checkout ${commitHash} && git branch -D ${upstreamBranchName} ${params.targetBranch} ${pullRequest.toRef.displayId}|| true"
                    }
                }
            }
        }

    }
    post {
        success {
            script {
                upstreamResult(
                    credentialsId: credential,
                    projectKey: params.projectKey,
                    repoSlug: params.repoSlug,
                    sourcePrId: params.sourcePullRequestId,
                    upstreamBranchName: upstreamBranchName,
                    state: 'success'
                )
            }
        }
        failure {
            script {
                try {
                    def authorPr = pullRequest.author.user.name
                    def fields = [
                        project          : [
                            id: jira.getProject('cab-sa-pyjobs01', bitbucket.getJiraKeyByLogin(credential, authorPr)).id
                        ],
                        summary          : "Задача на перелитие ПРа ${params.sourcePullRequestId}",
                        description      : "Этот ПР необходимо перелить в ветку ${params.targetBranch} самостоятельно, " +
                            "потому что робот по какой то причине не смог его перелить автоматом " +
                            "${Const.BITBUCKET_SERVER_INSTANCE_URL}/projects/${params.projectKey}" +
                            "/repos/${params.repoSlug}/pull-requests/${params.sourcePullRequestId}/overview",
                        customfield_10006: params.epicKey,
                        labels           : [pullRequest.toRef.displayId, params.targetBranch],
                        issuetype        : [
                            "id": 3
                        ],
                        assignee         : [
                            "name": authorPr
                        ]
                    ]
                    def jiraIssue = jira.createIssue('cab-sa-pyjobs01', fields)
                    upstreamResult(
                        credentialsId: credential,
                        projectKey: params.projectKey,
                        repoSlug: params.repoSlug,
                        sourcePrId: params.sourcePullRequestId,
                        jiraIssueLink: jiraIssue,
                        state: 'jira-issue'
                    )
                } catch (e) {
                    println(e)
                    upstreamResult(
                        credentialsId: credential,
                        projectKey: params.projectKey,
                        repoSlug: params.repoSlug,
                        sourcePrId: params.sourcePullRequestId,
                        state: 'failed'
                    )
                }
            }
        }
        cleanup {
            script {
                cleanWs()
            }
        }
    }
}
