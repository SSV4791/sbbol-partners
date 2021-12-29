import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

def projectLog = ''

pipeline {
    agent {
        label 'ufs-release'
    }
// Если нужна периодическая сборка то можно расскоментить строки ниже. Документация https://www.jenkins.io/doc/book/pipeline/syntax/#triggers
//    triggers {
//        cron('0 1 * * *')
//    }
    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'branch', defaultValue: 'develop', description: 'Ветка для сборки образа')
        choice(name: 'credentialID', choices: ['DS_CAB-SA-CI000825'], description: 'ТУЗ, используемая для сборки')
        string(name: 'istio_tag', defaultValue: '01.000.03', description: 'Тег для шаблонов istio')
        string(name: 'commitOrTag', description: 'Хэш коммита или тэг от которого формируется release-notes')
        booleanParam(name: 'checkmarx', defaultValue: false, description: 'Прохождение проверки Checkmarx')
        booleanParam(name: 'reverseAndPublish', defaultValue: false)
        choice(name: 'build_type', choices: ['develop', 'release'], description: "Тип сборки")
    }
    environment {
        NEXUS_CREDS = credentials("${params.credentialID}")
        SONAR_TOKEN = credentials('sonar-token-partners')
        // Паттерн версии для определения последней сборки по тегу. В данном случае будет искаться последняя сборка 01.000.00_хххх
        VERSION_PATTERN = /\d{2}\.\d{3}\.\d{2}_\d{4}/
        // Паттерн, по которому определяется начальная версия, если поиск по VERSION_PATTERN ничего не нашел
        INITIAL_VERSION = '01.002.00_0005'
        // internal envs
        DOCKER_IMAGE_REPOSITORY = ''
        DOCKER_IMAGE_NAME = ''
        DOCKER_IMAGE_HASH = ''
        ARTIFACT_NAME_OS = ''
        ARTIFACT_NAME_DOCS = ''
        ARTIFACT_NAME_LOCK = ''
        VERSION = ''
        LAST_VERSION = ''
        LATEST_COMMIT_HASH = ''
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

        /**
         * Клонирует указанный git branch
         */
        stage('Checkout git') {
            steps {
                script {
                    LATEST_COMMIT_HASH = git.checkoutRef('bitbucket-dbo-key', GIT_PROJECT, GIT_REPOSITORY, branch)
                }
            }
        }

        /**
         * Вычисляет версию текущей сборки
         */
        stage('Evaluate version') {
            steps {
                script {
                    // Ищем список всех тэгов, удовлетворяющих нашему паттерну версий
                    List versionTags = git.tags().findAll { it.matches(VERSION_PATTERN) }.sort()
                    LAST_VERSION = versionTags.isEmpty() ? '' : versionTags.last()

                    // Инкрементируем последнюю версию и устанавливаем ее, как текущую версию сборки
                    def versions = (LAST_VERSION ?: INITIAL_VERSION).split("_")
                    def nextBuildNumber = (versions[1].toInteger() + 1).toString().padLeft(4, '0')
                    VERSION = "${versions[0]}_${nextBuildNumber}"
                }
            }
        }

        /**
         * Push'им git tag с версией и устанавливаем в описание jenkins job собираемую версию
         */
        stage('Set version') {
            steps {
                script {
                    // Ставим git tag с версией сборки на текущий commit
                    git.tag('bitbucket-dbo-key', VERSION)
                    currentBuild.displayName += " D-$VERSION"
                    rtp stableText: "<h1>Build number: D-$VERSION</h1>"
                }
            }
        }

        /**
         * Собираем проект
         */
        stage('Build') {
            steps {
                script {
                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, params.credentialID)
                        .volume("${WORKSPACE}", "/build")
                        .extra("-w /build")
                        .cpu(2)
                        .memory("2g")
                        .image(BUILD_JAVA_DOCKER_IMAGE)
                        .cmd('./gradlew ' +
                            "-PnexusLogin=${NEXUS_CREDS_USR} " +
                            "-PnexusPassword=${NEXUS_CREDS_PSW} " +
                            "-Pversion=${VERSION} " +
                            "-Dsonar.login=${SONAR_TOKEN} " +
                            "-Dsonar.branch.name=${params.branch} " +
                            'build sonarqube --parallel'
                        )
                        .run()
                }
            }
        }

        stage('Build and Push docker image') {
            steps {
                script {
                    switch (params.build_type) {
                        case "release":
                            DOCKER_IMAGE_REPOSITORY = DOCKER_IMAGE_REPOSITORY_PROD
                            break
                        case "develop":
                            DOCKER_IMAGE_REPOSITORY = DOCKER_IMAGE_REPOSITORY_DEV
                            break
                    }
                    DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKER_IMAGE_REPOSITORY}/${ARTIFACT_ID}:${VERSION}"
                    docker.withRegistry(Const.OPENSHIFT_REGISTRY, params.credentialID) {
                        docker.build(DOCKER_IMAGE_NAME, "--force-rm .").push()
                    }
                }
            }
        }

        /**
         * Подготавливаем шаблоны OpenShift для публикации
         */
        stage('Prepare Openshift manifest') {
            steps {
                script {
                    dir('openshift') {
                        istio.getOSTemplates('bitbucket-dbo-key', 'istio', 'openshift', params.istio_tag, './istio')
                        git.raw(params.credentialID, 'cibufs', 'sbbol-params', 'master', "${ARTIFACT_ID}/${params.branch}/params.yml")
                        def repoDigest = sh(script: "docker inspect ${DOCKER_IMAGE_NAME} --format='{{index .RepoDigests 0}}'", returnStdout: true).trim()
                        DOCKER_IMAGE_HASH = repoDigest.split('@').last()
                        log.info('Docker image hash: ' + DOCKER_IMAGE_HASH)
                    }
                }
            }
        }

        /**
         * Публикуем артефакт в нексус
         */
        stage('Publish artifacts') {
            steps {
                script {
                    new DockerRunBuilder(this)
                        .registry(Const.OPENSHIFT_REGISTRY, params.credentialID)
                        .env("GRADLE_USER_HOME", '/build/.gradle')
                        .volume("${WORKSPACE}", "/build")
                        .extra("-w /build")
                        .cpu(1)
                        .memory("1g")
                        .image(BUILD_JAVA_DOCKER_IMAGE)
                        .cmd('./gradlew ' +
                            "-PnexusLogin=${NEXUS_CREDS_USR} " +
                            "-PnexusPassword='${NEXUS_CREDS_PSW}' " +
                            "-Pversion=D-${VERSION} " +
                            "-PdockerImage='${DOCKER_IMAGE_REPOSITORY}/${ARTIFACT_ID}@${DOCKER_IMAGE_HASH}' " +
                            "${params.build_type} " +
                            "${params.reverseAndPublish ? 'reverseAndPublish' : ''}"
                        )
                        .run()
                }
            }
        }

        /**
         * Собирает артефакт с lock файлами
         */
        stage('Prepare locks') {
            when { expression { params.type == 'release' } }
            steps {
                script {
                    ARTIFACT_NAME_LOCK = "${ARTIFACT_ID}_locks-${VERSION}.zip"
                    sh 'mkdir -p distrib'
                    dir('gradle') {
                        sh "zip -rq ${WORKSPACE}/distrib/$ARTIFACT_NAME_LOCK dependency-locks"
                        dir('wrapper') {
                            sh "zip -rq ${WORKSPACE}/distrib/$ARTIFACT_NAME_LOCK gradle-wrapper.properties"
                        }
                    }
                }
            }
        }

        /**
         * Собирает release notes
         */
        stage('Create release notes') {
            steps {
                script {
                    //преобразование введенного значения в полный хэш коммита fromCommit
                    def fromCommit = ''
                    def comOrTag = commitOrTag.trim()
                    if (comOrTag.isEmpty()) {
                        fromCommit = git.tag2hash(LAST_VERSION) // конвертируем тэг последней версии в hash коммита
                        log.info("Значение в поле commitOrTag не введено, по умолчанию commitOrTag = ${LAST_VERSION}" +
                            " - последняя версия сборки")
                    } else {
                        fromCommit = git.convert2hash(comOrTag, VERSION_PATTERN)
                    }
                    projectLog = sh(
                        returnStdout: true,
                        script: "git log --oneline --no-merges --pretty=tformat:'%s' " +
                            "${fromCommit ? fromCommit + '..HEAD' : ''}"
                    )
                    def projectUrl = "https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/${GIT_PROJECT}/repos/${GIT_REPOSITORY}/" as String
                    releaseNotes = createReleaseNotes(projectLog, LATEST_COMMIT_HASH, projectUrl, fromCommit)
                }
            }
        }

        /**
         * Публикует собранные выше артефакты в nexus
         */
        stage('Publish') {
            when { expression { params.type == 'release' } }
            steps {
                script {
                    dir('distrib') {
                        qgm.publishReleaseNotes(GROUP_ID, ARTIFACT_ID, "D-$VERSION", releaseNotes, params.credentialID)
                        nexus.publishZip(GROUP_ID, ARTIFACT_ID, "distrib.lock", ARTIFACT_NAME_LOCK, "D-$VERSION", params.credentialID)
                    }
                }
            }
        }

        /**
         * Публикует документацию на сервер с документацией
         */
        stage('Publish documentation') {
            steps {
                script {
                    def docsPath = "${ARTIFACT_ID}/${env.BRANCH_NAME}" as String // путь для публикации документации
                    docs.publish('documentation-publisher', 'docs/build/docs/', docsPath)
                }
            }
        }

        /**
         * Анализ кода checkmarx
         * @see http://confluence.sberbank.ru/display/OTIB/SAST
         */
        stage('Checkmarx code analyze') {
            when { expression { params.checkmarx } }
            steps {
                script {
                    def devsecopsConfig = readYaml(file: 'jenkins/resources/devsecops-config.yml')
                    String repoUrl = "${Const.BITBUCKET_SERVER_INSTANCE_URL}/scm/${GIT_PROJECT.toLowerCase()}/${GIT_REPOSITORY}.git"
                    library('ru.sbrf.devsecops@master')
                    runOSS(devsecopsConfig, repoUrl, "${params.branch}/${GIT_REPOSITORY}", LATEST_COMMIT_HASH)
                    runSastCx(devsecopsConfig, repoUrl, "${params.branch}/${GIT_REPOSITORY}", LATEST_COMMIT_HASH)
                    checkmarx.checkStatus(Const.SAST_QG_URL, LATEST_COMMIT_HASH, VERSION)
                    def QGstatus = getOSSQGFlag(LATEST_COMMIT_HASH)
                    log.info("OSS_RUN:${QGstatus.OSS_RUN} OSS_PASS:${QGstatus.OSS_PASS} OSS_HIGH_PASS:${QGstatus.OSS_HIGH_PASS} OSS_MEDIUM_PASS:${QGstatus.OSS_MEDIUM_PASS}")
                }
            }
        }

        /**
         * Публикует технические флаги о сборке
         */
        stage('Push technical flags') {
            steps {
                script {
                    dpm.publishFlags("D-$VERSION", ARTIFACT_ID, GROUP_ID, ["bvt", "ci", "smart_regress_ift", "smart_regress_st", "smoke_ift", "smoke_st"], params.credentialID)
                }
            }
        }

        /**
         * Проставляет в таски jira fix build
         */
        stage('Set fix build') {
            steps {
                script {
                    def jiraIssues = [] as Set
                    projectLog.trim().split('\n').each { issue ->
                        def jiraIssue = (issue =~ /[A-Za-z0-9]+-+[0-9]+/)
                        if (jiraIssue.find()) {
                            jiraIssues << jiraIssue.group(0)
                        }
                    }

                    jiraIssues.each { jiraIssue ->
                        try {
                            jira.setFixBuild(jiraIssue, VERSION, params.credentialID)
                        } catch (e) {
                            log.error("Failed to update fixBuild for ${jiraIssue}: ${e}")
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: "distrib/*.zip", allowEmptyArchive: true
            archiveArtifacts artifacts: "build/*.zip", allowEmptyArchive: true
        }
        /**
         * Обязательно подчищаем за собой
         */
        cleanup {
            cleanWs()
            sh "docker rmi -f ${DOCKER_IMAGE_NAME} || true"
        }
    }
}
