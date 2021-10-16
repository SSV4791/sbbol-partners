import ru.sbrf.ufs.pipeline.Const
import ru.sbrf.ufs.pipeline.docker.DockerRunBuilder

@Library(['ufs-jobs@master']) _

def latestCommitHash = ''
def projectLog = ''

pipeline {
    agent {
        label 'ufs-release'
    }
    options {
        timeout(time: 180, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'branch', defaultValue: 'develop', description: 'Ветка для сборки образа')
        choice(name: 'credentialID', choices: ['DS_CAB-SA-CI000825'], description: 'ТУЗ, используемая для сборки')
        booleanParam(name: 'checkmarx', defaultValue: false, description: 'Прохождение проверки Checkmarx')
    }
    environment {
        NEXUS_CREDS = credentials("${params.credentialID}")
        SONAR_TOKEN = credentials('sonar-token-partners')
        // Паттерн версии для определения последней сборки по тегу. В данном случае будет искаться последняя сборка 01.000.00_хххх
        VERSION_PATTERN = /01\.000\.00_\d{4}/
        // Паттерн, по которому определяется начальная версия, если поиск по VERSION_PATTERN ничего не нашел
        INITIAL_VERSION = '01.002.00_0005'
        // internal envs
        IMAGE_NAME = ''
        ARTIFACT_NAME_OS = ''
        ARTIFACT_NAME_DOCS = ''
        ARTIFACT_NAME_LOCK = ''
        VERSION = ''
        LAST_VERSION = ''
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
         * Push'им git tag с версией
         */
        stage('Set version') {
            steps {
                script {
                    // Ставим git tag с версией сборки на текущий commit
                    git.tag('bitbucket-dbo-key', VERSION)
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

        /**
         * Устанавливает в описание jenkins job собираемую версию
         */
        stage('Set display') {
            steps {
                script {
                    currentBuild.displayName += " $VERSION"
                    rtp stableText: "<h1>Build number: $VERSION</h1>"
                }
            }
        }

        /**
         * Собирает и публикует docker образ в registry
         */
        stage('Build and Push docker image') {
            steps {
                script {
                    IMAGE_NAME = "${DOCKER_REGISTRY}/${BASE_IMAGE_NAME}/${ARTIFACT_ID}:${VERSION}"
                    docker.withRegistry(Const.OPENSHIFT_REGISTRY, params.credentialID) {
                        docker.build(IMAGE_NAME, "--force-rm .").push()
                    }
                }
            }
        }

        /**
         * Собирает артефакт с шаблонами OpenShift
         */
        stage('Prepare Openshift manifest') {
            steps {
                script {
                    dir('openshift') {
                        def repoDigest = sh(script: "docker inspect ${IMAGE_NAME} --format='{{index .RepoDigests 0}}'", returnStdout: true).trim()
                        def imageHash = repoDigest.split('@').last()
                        log.info('Docker image hash: ' + imageHash)
                        sh "sed -i 's/\${imageNameWithDigest}/${ARTIFACT_ID}@${imageHash}/' configs/Deployment/deployment-partners.yml"
                        sh "sed -i 's/\${imageVersion}/${VERSION}/' configs/Deployment/deployment-partners.yml"
                        ARTIFACT_NAME_OS = "${ARTIFACT_ID}_os-${VERSION}.zip"
                        // copy liquibase
                        sh "cp -r ../runner/src/main/resources/db/changelog db"
                        sh "mkdir ${WORKSPACE}/distrib"
                        sh "zip -rq ${WORKSPACE}/distrib/${ARTIFACT_NAME_OS} *"
                    }
                }
            }
        }

        /**
         * Собирает артефакт с документацией
         */
        stage('Prepare docs') {
            steps {
                script {
                    ARTIFACT_NAME_DOCS = "${ARTIFACT_ID}_docs-${VERSION}.zip"
                    dir('docs/build') {
                        sh "zip -rq ${WORKSPACE}/distrib/$ARTIFACT_NAME_DOCS docs"
                    }
                }
            }
        }

        /**
         * Собирает артефакт с lock файлами
         */
        stage('Prepare locks') {
            steps {
                script {
                    ARTIFACT_NAME_LOCK = "${ARTIFACT_ID}_locks-${VERSION}.zip"
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
                    projectLog = sh(
                        returnStdout: true,
                        script: "git log --oneline --no-merges --pretty=tformat:'%s' " +
                            "${LAST_VERSION ? LAST_VERSION + '..HEAD' : ''}"
                    )
                    dir('distrib') {
                        def projectUrl = "https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/${GIT_PROJECT}/repos/${GIT_REPOSITORY}/" as String
                        releaseNotes = createReleaseNotes(projectLog, latestCommitHash, projectUrl)
                        sh "zip -q release_notes-${VERSION}.zip release-notes"
                    }
                }
            }
        }

        /**
         * Публикует собранные выше артефакты в nexus
         */
        stage('Publish') {
            steps {
                script {
                    dir('distrib') {
                        publishDev(
                            credentialId: params.credentialID,
                            repository: "corp-releases",
                            groupId: "ru.sberbank.pprb.sbbol.partners",
                            artifactId: ARTIFACT_ID,
                            version: "D-${VERSION}",
                            extension: 'zip',
                            packaging: 'zip',
                            classifier: "distrib",
                            file: ARTIFACT_NAME_OS
                        )
                        log.info("Distrib url: http://nexus.sigma.sbrf.ru:8099/nexus/service/local/repositories/corp-releases/content/ru/sberbank/pprb/sbbol/partners/partners/D-${VERSION}/partners-D-${VERSION}-distrib.zip")
                        qgm.publishReleaseNotes(GROUP_ID, ARTIFACT_ID, VERSION, releaseNotes, params.credentialID)
                        nexus.publishZip(GROUP_ID, ARTIFACT_ID, "distrib.openshift", ARTIFACT_NAME_OS, VERSION, params.credentialID)
                        nexus.publishZip(GROUP_ID, ARTIFACT_ID, "distrib.docs", ARTIFACT_NAME_DOCS, VERSION, params.credentialID)
                        nexus.publishZip(GROUP_ID, ARTIFACT_ID, "distrib.lock", ARTIFACT_NAME_LOCK, VERSION, params.credentialID)
                        archiveArtifacts artifacts: "*.zip"
                        log.info("Prod url: https://sbrf-nexus.ca.sbrf.ru/nexus/content/repositories/Nexus_PROD/content/Nexus_PROD/CI02792425_sbbol-partners/partners/D-${VERSION}/partners-D-${VERSION}-distrib.openshift.zip")
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
         */
        stage('Checkmarx code analyze') {
            when { expression { params.checkmarx } }
            steps {
                script {
                    def devsecopsConfig = readYaml(file: 'jenkins/resourse/devsecops-config.yml')
                    String repoUrl = "${Const.BITBUCKET_SERVER_INSTANCE_URL}/scm/${GIT_PROJECT.toLowerCase()}/${GIT_REPOSITORY}.git"
                    library('ru.sbrf.devsecops@master')
                    runOSS(devsecopsConfig, repoUrl, "${params.branch}/${GIT_REPOSITORY}", latestCommitHash)
                    runSastCx(devsecopsConfig, repoUrl, "${params.branch}/${GIT_REPOSITORY}", latestCommitHash)

                    /**
                     * В функции checkStatus четвертым параметром можно выставить максимальное время ожидания ответа (в секундах)
                     * Если не дождаться ответа, то будет добавлен флаг о непрохождении этой проверки
                     * Этот параметр индивидуален и зависит от размера вашего приложения(кода)
                     * По умолчанию стоит 5 минут
                     * В примере ниже выставляем ожидание 2 минуты
                     *   checkmarx.checkStatus(Const.SAST_QG_URL, latestCommitHash, VERSION, 120)
                     */
                    checkmarx.checkStatus(Const.SAST_QG_URL, latestCommitHash, VERSION, 600)
                    def QGstatus = getOSSQGFlag(latestCommitHash)
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
                    dpm.publishFlags(VERSION, ARTIFACT_ID, GROUP_ID, ["bvt", "ci", "smart_regress_ift", "smart_regress_st", "smoke_ift", "smoke_st"], params.credentialID)
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
        /**
         * Обязательно подчищаем за собой
         */
        cleanup {
            cleanWs()
            sh "docker rmi -f ${IMAGE_NAME} || true"
        }
    }
}

/**
 * Функция по публикации в разработческий Nexus. Видоизмененная копия
 * https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/CIBUFS/repos/ufs-jobs/browse/vars/nexus.groovy
 *
 * @param credentialId jenkins credential name
 * @param repository Репозиторий nexus
 * @param groupId Имя группы артефакта
 * @param artifactId Имя артефакта
 * @param version Версия
 * @param extension Расширение файла (json/xml/etc)
 * @param classifier Классификатор (суффикс) артефакта
 * @param packaging Расширение архива (zip/rar/etc)
 * @param file Имя/путь файла
 */
def publishDev(Map params) {
    def response = ""
    def code = ""
    withCredentials([usernamePassword(credentialsId: params.credentialId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        String request = "curl -vk" +
            " -w 'http code:%{http_code}'" +
            " -u ${USERNAME}:${PASSWORD}" +
            " -F r=${params.repository}" +
            " -F g=${params.groupId}" +
            " -F a=${params.artifactId}" +
            " -F v=${params.version}" +
            " -F p=${params.packaging}" +
            " -F c=${params.classifier}" +
            " -F e=${params.extension}" +
            " -F file=@${params.file}" +
            " -F hasPom=false" +
            " https://nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content"

        response = sh(returnStdout: true, script: request)
        log.info("Response: ${response}")
        code = sh(returnStdout: true, script: "echo ${response} | grep 'http code:' ")

    }
    if (params.extension == 'flag' && params.classifier.contains('ift')) return
    // Костыль! Не работает публикация некоторых флагов. Мешает сборке релизов.
    def arr = code.split(":")
    if (code.trim() == '' || arr.length == 0 || arr[arr.length - 1].trim() != '201') {
        error("Failed publish to Nexus")
    }
}
