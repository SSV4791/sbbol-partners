import groovy.json.JsonOutput
import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _

def latestCommitHash = ''

pipeline {
    agent {
        label 'ufs-release'
    }
    options {
        timeout(time: 180, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'branch', defaultValue: 'release-2.000.00', description: 'Ветка, с которой собирается поставка (если проставлен флаг fullBuild)')
        booleanParam(name: 'release', defaultValue: true, description: 'Выпуск релизной сборки')
        booleanParam(name: 'needQG', defaultValue: true, description: 'QG')
        choice(name: 'jenkins', choices: 'SBBOL\nPPRBAC', description: 'В каком jenkins запускать job CustomerBuild')
    }
    environment {
        GIT_PROJECT = 'CIBPPRB'
        GIT_REPOSITORY = 'sbbol-partners'
        GIT_LINK = 'ssh://git@10.56.5.65:8878/cibpprb/sbbol-partners.git'
        GROUP_ID = 'Nexus_PROD.CI02792425_sbbol-partners'
        CUSTOMER_ARTIFACT_ID = 'partners'
        DATASPACE_ARTIFACT_ID = 'dataspace-apps-partners'
        ARTIFACT_NAME_OS = ''
        CUSTOMER_ARCHIVE_NAME = 'customer-distrib.zip'
        DATASPACE_ARCHIVE_NAME = 'dataspace-distrib.zip'
        VERSION_PATTERN = /\d{2}\.\d{3}\.\d{2}_\d{4}/
        // Паттерн, по которому выбираются версии для инкремента последней при сборке
        INITIAL_VERSION = '02.000.00_0001'
        VERSION = ''
        LAST_VERSION = ''
        DATASPACE_CONFIGS = ''
        NEXUSSBRF_RELEASE_REPOSITORY = 'https://sbrf-nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        DEV_REPOSITORY = 'https://nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        PROJECT_URL = "https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/${GIT_PROJECT}/repos/${GIT_REPOSITORY}/"
        CUSTOMER_DISTRIB_URL = ''
        DATASPACE_DISTRIB_URL = ''
        CUSTOMER_BUILDER_URL = ''
        NEXUS_CREDENTIALS_ID = 'DS_CAB-SA-CI000825'
        JENKINS_CREDENTIALS_ID = 'CAB-SA-CI000825-sbt-jenkins-sigma'
        BITBUCKET_CREDENTIALS_ID = 'bitbucket-dbo-key'
    }

    stages {

        stage('Input params validation') {
            steps {
                script {
                    switch (params.jenkins) {
                        case "PPRBAC":
                            CUSTOMER_BUILDER_URL = "https://sbt-jenkins.sigma.sbrf.ru/job/PPRBAC/job/Openshift/job/DataSpace/job/CustomerBuilder"
                            DATASPACE_CONFIGS = './config/default.yml,./config/pprbac/commons.yml'
                            break
                        case "SBBOL":
                            CUSTOMER_BUILDER_URL = "https://sbt-jenkins.sigma.sbrf.ru/job/SBBOL/job/DataSpace/job/CustomerBuilder"
                            DATASPACE_CONFIGS = './config/default.yml,./config/sbbol/commons.yml'
                            break
                    }
                }
            }
        }

        /**
         * Клонирует указанный git branch
         */
        stage('Checkout git') {
            steps {
                script {
                    latestCommitHash = git.checkoutRef(BITBUCKET_CREDENTIALS_ID, GIT_PROJECT, GIT_REPOSITORY, params.branch)
                }
            }
        }

        stage('Detect build version') {
            steps {
                script {
                    sh 'mkdir distrib'
                    // Ищем список всех тэгов, удовлетворяющих нашему паттерну версий
                    List versionTags = git.tags().findAll { it.matches(VERSION_PATTERN) }.sort()
                    LAST_VERSION = versionTags.isEmpty() ? '' : versionTags.last()

                    // Инкрементируем последнюю версию и устанавливаем ее, как текущую версию сборки
                    def versions = (LAST_VERSION ?: INITIAL_VERSION).split("_")
                    def nextBuildNumber = (versions[1].toInteger() + 1).toString().padLeft(4, '0')
                    VERSION = "${versions[0]}_${nextBuildNumber}"
                    log.info('Build version: ' + VERSION)
                }
            }
        }

        stage('Trigger CustomerBuilder') {
            steps {
                script {

                    def parameters = [
                        "isRelease"              : params.release,
                        "jobMode"                : "Deployer",
                        "gitModel"               : GIT_LINK,
                        "branch"                 : params.branch,
                        "pathToConfigFile"       : DATASPACE_CONFIGS,
                        "isIncludeCoreWithSearch": true,
                        "isIncludeOnlyCore"      : false,
                        "isIncludeOnlySearch"    : false,
                        "isIncludeGigabas"       : true,
                        "isIncludeStateMachine"  : false,
                        "isIncludeDuplication"   : false,
                        "buildClient"            : true,
                        "needInstallToOS"        : false,
                        "needQG"                 : params.needQG,
                        "clientDeployerOptions"  : params.release ? "-Dversion.forceVersion=${VERSION}" : ""
                    ]
                    def paramString = new StringBuilder()
                    for (param in parameters) {
                        if (paramString.length() != 0) {
                            paramString.append('&')
                        }
                        paramString.append(param.key)
                            .append('=')
                            .append(URLEncoder.encode(param.value as String, "UTF-8"))
                    }
                    def triggerBuildResponse = httpRequest(
                        httpMode: 'POST',
                        authentication: "${JENKINS_CREDENTIALS_ID}",
                        ignoreSslErrors: true,
                        quiet: true,
                        consoleLogResponseBody: false,
                        url: "${CUSTOMER_BUILDER_URL}/buildWithParameters?${paramString}"
                    )
                    def queueLink = triggerBuildResponse.headers["Location"][0]
                    log.info("CustomerBuild submitted. Queue link: ${queueLink}")

                    def timeoutSeconds = 1
                    String buildLink = null
                    while (!buildLink) {
                        def queueResponse = httpRequest(
                            authentication: "${JENKINS_CREDENTIALS_ID}",
                            ignoreSslErrors: true,
                            quiet: true,
                            consoleLogResponseBody: false,
                            url: "${queueLink}api/json")
                        def queue = readJSON(text: queueResponse.content)
                        buildLink = queue?.executable?.url
                        if (!buildLink) {
                            log.info("Waiting for build to start...")
                            sleep(timeoutSeconds)
                            timeoutSeconds++
                        }
                    }
                    log.info("CustomerBuilder started: ${buildLink}")

                    def finished = false
                    def result = null
                    while (!finished) {
                        def buildResponse = httpRequest(
                            authentication: "${JENKINS_CREDENTIALS_ID}",
                            ignoreSslErrors: true,
                            quiet: true,
                            consoleLogResponseBody: false,
                            url: "${buildLink}api/json")
                        def buildInfo = readJSON(text: buildResponse.content)
                        finished = !buildInfo?.building
                        if (!finished) {
                            log.info("Waiting for build to complete...")
                            log.info("Build link: ${buildLink}")
                            sleep(30)
                        } else {
                            result = buildInfo
                        }
                    }
                    log.info("CustomerBuilder finished. Result ${result?.result}")

                    if ("SUCCESS" == result.result) {
                        def description = result.description

                        def regex = /.*<br> jobResult = ([^\r^\n]*).*/
                        def jobResultText = (description =~ regex)[0][1]
                        log.info("Job result: ${jobResultText}")
                        def jobResult = readJSON(text: jobResultText)

                        log.info("DataSpace distrib link: ${jobResult.rootKey.dataSpaceDistribution}")
                        log.info("Customer distrib link: ${jobResult.rootKey.customerDistribution}")
                        CUSTOMER_DISTRIB_URL = jobResult.rootKey.customerDistribution
                        DATASPACE_DISTRIB_URL = jobResult.rootKey.dataSpaceDistribution
                    } else {
                        error("Unsuccessfull build. Check logs of CustomerBuilder ${buildLink}")
                    }
                }
            }
        }

        stage('Download distribs') {
            steps {
                script {
                    log.info("Downloading customer distrib ${CUSTOMER_DISTRIB_URL}")
                    httpRequest authentication: "${NEXUS_CREDENTIALS_ID}",
                        outputFile: CUSTOMER_ARCHIVE_NAME,
                        responseHandle: 'NONE',
                        url: "${CUSTOMER_DISTRIB_URL}"
                    log.info("Downloading dataspace distrib ${DATASPACE_DISTRIB_URL}")
                    httpRequest authentication: "${NEXUS_CREDENTIALS_ID}",
                        outputFile: DATASPACE_ARCHIVE_NAME,
                        responseHandle: 'NONE',
                        url: "${DATASPACE_DISTRIB_URL}"
                }
            }
        }

        stage('Prepare install_eip archive') {
            steps {
                script {
                    dir('distrib') {
                        sh "unzip ../${CUSTOMER_ARCHIVE_NAME}"
                        sh "unzip ../${DATASPACE_ARCHIVE_NAME} -d ./dataspace"

                        // extract version
                        def dataspaceVersion = sh(
                            script: "cat dataspace/configs/dataspace-core-template.yaml" +
                                " | sed -n 's/^.*version: \\(.*\\).*\$/\\1/p'" +
                                " | head -1",
                            returnStdout: true).trim()
                        sh "echo \"Dataspace version: ${dataspaceVersion}\""

                        // remove version from resources names
                        sh "sed -i 's/\${MODULE_NAME}-${dataspaceVersion}/\${MODULE_NAME}/' dataspace/configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${MODULE_NAME}-${dataspaceVersion}/\${MODULE_NAME}/' dataspace/configs/dataspace-gigabas-template.yaml"

                        // package archive
                        ARTIFACT_NAME_OS = "partners-${VERSION}.zip"
                        sh "zip -rq ${WORKSPACE}/distrib/${ARTIFACT_NAME_OS} *"
                    }
                }
            }
        }

        stage('Publish') {
            steps {
                script {
                    dir('distrib') {
                        log.info("Publishing artifact to ${DEV_REPOSITORY}")
                        publishDev(
                            credentialId: "${NEXUS_CREDENTIALS_ID}",
                            repository: "corp-releases",
                            groupId: "ru.sberbank.pprb.sbbol.partners",
                            artifactId: CUSTOMER_ARTIFACT_ID,
                            version: "D-${VERSION}",
                            extension: 'zip',
                            packaging: 'zip',
                            classifier: "distrib",
                            file: ARTIFACT_NAME_OS
                        )
                        log.info("Successfully published to https://nexus.sigma.sbrf.ru/nexus/content/repositories/corp-releases/ru/sberbank/pprb/sbbol/partners/partners/D-${VERSION}/")
                        log.info("Distrib url: http://nexus.sigma.sbrf.ru:8099/nexus/service/local/repositories/corp-releases/content/ru/sberbank/pprb/sbbol/partners/partners/D-${VERSION}/partners-D-${VERSION}-distrib.zip")
                        if (params.release) {
                            log.info("Uploading CustomerBuilder distribs to sbrf-nexus")

                            nexus.publishZip(GROUP_ID, DATASPACE_ARTIFACT_ID, "distrib", "../${DATASPACE_ARCHIVE_NAME}", VERSION)
                            log.info("Successfully published to ${getSbrfNexusLink(DATASPACE_ARTIFACT_ID, VERSION, GROUP_ID.replaceAll('\\.', '/'))}")
                            nexus.publishZip(GROUP_ID, CUSTOMER_ARTIFACT_ID, "distrib", "../${CUSTOMER_ARCHIVE_NAME}", VERSION)
                            log.info("Successfully published to ${getSbrfNexusLink(CUSTOMER_ARTIFACT_ID, VERSION, GROUP_ID.replaceAll('\\.', '/'))}")

                            log.info("Publishing artifact to ${NEXUSSBRF_RELEASE_REPOSITORY}")
                            nexus.publishZip(GROUP_ID, CUSTOMER_ARTIFACT_ID, "distrib", ARTIFACT_NAME_OS, "${VERSION}-eip")
                            log.info("Successfully published to ${getSbrfNexusLink(CUSTOMER_ARTIFACT_ID, "${VERSION}-eip", GROUP_ID.replaceAll('\\.', '/'))}")
                        }
                        archiveArtifacts artifacts: "*.zip"
                    }
                }
            }
        }

        stage('Push technical flags') {
            when {
                expression { params.needQG }
            }
            steps {
                script {
                    boolean qgPassed = true
                    for (String artifactId in [DATASPACE_ARTIFACT_ID, CUSTOMER_ARTIFACT_ID]) {
                        def response = qgm.getFlagMap(
                            credId: "${NEXUS_CREDENTIALS_ID}",
                            repositoryId: 'Nexus_PROD',
                            groupId: GROUP_ID.replaceAll('\\.', '/'),
                            artifactId: DATASPACE_ARTIFACT_ID,
                            version: VERSION
                        )
                        log.info("QGM result for ${artifactId}: ${response}")
                        def success = true
                        def flags = ["ci", "sast", "oss", "meta"]
                        for (def flag : flags) {
                            if (response[flag] != "ok") {
                                success = false
                                break
                            }
                        }
                        if (!success) {
                            qgPassed = false
                            log.warn("QG unsuccessful for artifact ${artifactId}. Flags won't be published for current artifact")
                            break
                        }
                    }
                    log.info("QG result: ${qgPassed}")
                    if (qgPassed) {
                        dpm.publishFlags("${VERSION}-eip", CUSTOMER_ARTIFACT_ID, GROUP_ID, ["ci", "sast", "oss", "meta"])
                    }
                }
            }
        }

        stage('Push ReleaseNotes') {
            when {
                expression { params.release }
            }
            steps {
                script {
                    log.info("Last version: ${LAST_VERSION}")
                    def projectLog = sh(
                        returnStdout: true,
                        script: "git log --oneline --no-merges --pretty=tformat:'%s' " +
                            "${LAST_VERSION ? LAST_VERSION + '..HEAD' : ''}"
                    )
                    dir('distrib') {
                        def sbrfNexusCustomerLink = getSbrfNexusLink(CUSTOMER_ARTIFACT_ID, VERSION, GROUP_ID.replaceAll('\\.', '/'))
                        def sbrfNexusDataspaceLink = getSbrfNexusLink(DATASPACE_ARTIFACT_ID, VERSION, GROUP_ID.replaceAll('\\.', '/'))
                        releaseNotes = createReleaseNotesWithDescription(projectLog, latestCommitHash, PROJECT_URL, sbrfNexusCustomerLink, sbrfNexusDataspaceLink)
                        try {
                            nexus.publishReleaseNotes(GROUP_ID, CUSTOMER_ARTIFACT_ID, "${VERSION}-eip")
                        } catch (e) {
                            sendEmail('Surov.P.V@sberbank.ru', 'Failed push ReleaseNotes to nexus', e)
                        }
                        qgm.publishReleaseNotes(GROUP_ID, CUSTOMER_ARTIFACT_ID, "${VERSION}-eip", releaseNotes, NEXUS_CREDENTIALS_ID)
                        archiveArtifacts artifacts: "release-notes"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Ставим git tag с версией сборки на текущий commit
                git.tag(BITBUCKET_CREDENTIALS_ID, VERSION)
            }
        }
        cleanup {
            cleanWs()
        }
    }
}

static String getSbrfNexusLink(String artifactId, String version, String groupId) {
    return "https://sbrf-nexus.sigma.sbrf.ru/nexus/content/repositories/Nexus_PROD/${groupId}/${artifactId}/${version}/${artifactId}-${version}-distrib.zip"
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
            " ${DEV_REPOSITORY}"

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

/**
 * Функция по формированию ReleaseNotes. Видоизмененная копия
 * https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/CIBUFS/repos/ufs-jobs/browse/vars/createReleaseNotes.groovy
 * </br>
 * Отличается дополнительным блоком <pre>desc</pre> в ReleaseNotes с ссылками на артефакты,
 * полученные через CustomerBuilder.
 *
 * @param releaseNotes изменения, которые необходимо добавить в release-notes
 * @param latestCommit хеш последнего коммита
 * @param projectUrl ссылка на проект в BitBucket
 * @param customerDistribUrl ссылка на артефакт фабрики в sbrf-nexus (из CustomerBuilder)
 * @param dataspaceDistribUrl ссылка на артефакт dataspace в sbrf-nexus (из CustomerBuilder)
 */
def createReleaseNotesWithDescription(String releaseNotes, String latestCommit, String projectUrl,
                                      String customerDistribUrl, String dataspaceDistribUrl) {
    def data = [
        releaseNotes: [

        ],
        codeNotes   : [[
                           'commit'    : latestCommit,
                           'repository': projectUrl
                       ]],
        desc        : [
            'customerDistribUrl' : customerDistribUrl,
            'dataspaceDistribUrl': dataspaceDistribUrl
        ]
    ]

    def jiraIssues = [] as Set
    releaseNotes.trim().split('\n').each { releaseNote ->
        def jiraIssue = (releaseNote =~ /[A-Za-z0-9]+-+[0-9]+/)
        if (jiraIssue.find()) {
            jiraIssues << jiraIssue.group(0)
        }
    }

    jiraIssues.each { issue ->
        data.releaseNotes << [issue: issue]
    }

    def json = JsonOutput.toJson(data)
    log.info("ReleaseNotes: ${json}")
    writeFile file: 'release-notes', text: json
    return data
}
