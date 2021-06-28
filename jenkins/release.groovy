import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _


pipeline {
    agent {
        label 'ufs-release'
    }
    options {
        timeout(time: 180, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'version', defaultValue: '2.000.00', description: 'Версия сборки')
        booleanParam(name: 'fullBuild', defaultValue: true, description: 'Полная сборка с вызовом CustomerBuilder (обязательно для релизной сборки)')
        string(name: 'branch', defaultValue: 'release-2.000.00', description: 'Ветка, с которой собирается поставка (если проставлен флаг fullBuild)')
        string(name: 'dataspaceDistrib', defaultValue: '', description: 'Ссылка на дистрибутив dataspace (если не проставлен флаг fullBuild)')
        string(name: 'customerDistrib', defaultValue: '', description: 'Ссылка на дистрибутив фабрики (если не проставлен флаг fullBuild)')
        booleanParam(name: 'dynamicVersion', defaultValue: true, description: 'Добавлять номер сборки к версии (2.000.00_00XX)')
        booleanParam(name: 'release', defaultValue: true, description: 'Выпуск релизной сборки')
        booleanParam(name: 'needQG', defaultValue: true, description: 'QG')
        choice(name: 'jenkins', choices: 'SBBOL\nPPRBAC', description: 'В каком jenkins запускать job CustomerBuild')
    }
    environment {
        GIT_PROJECT = 'CIBPPRB'
        GIT_REPOSITORY = 'sbbol-partners'
        GIT_LINK = 'ssh://git@10.56.5.65:8878/cibpprb/sbbol-partners.git'
        GROUP_ID = 'Nexus_PROD.CI02792425_sbbol-partners'
        ARTIFACT_ID = 'partners'
        ARTIFACT_NAME_OS = ''
        VERSION = ''
        DATASPACE_CONFIGS = ''
        NEXUSSBRF_RELEASE_REPOSITORY = 'https://sbrf-nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        DEV_REPOSITORY = 'https://nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        PROJECT_URL = "https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/${GIT_PROJECT}/repos/${GIT_REPOSITORY}/"
        CUSTOMER_DISTRIB_URL = ''
        DATASPACE_DISTRIB_URL = ''
        CUSTOMER_BUILDER_URL = ''
        NEXUS_CREDENTIALS_ID = 'DS_CAB-SA-CI000825'
        JENKINS_CREDENTIALS_ID = 'CAB-SA-CI000825-sbt-jenkins-sigma'
    }

    stages {

        stage('Input params validation') {
            steps {
                script {
                    if (!params.version) {
                        error("Version is required")
                    } else if (params.fullBuild && !params.branch) {
                        error("Branch is required for fullBuild")
                    } else if (!params.fullBuild) {
                        if (!params.customerDistrib) {
                            error("customerDistrib is required (flag fullBuild is not set)")
                        }
                        if (!params.dataspaceDistrib) {
                            error("dataspaceDistrib is required (flag fullBuild is not set)")
                        }
                        if (params.release) {
                            error("fullBuild should be selected for release distrib (QG checks in CustomerBuilder are required)")
                        }
                    }
                    switch (params.jenkins) {
                        case "PPRBAC":
                            CUSTOMER_BUILDER_URL = "https://sbt-jenkins.sigma.sbrf.ru/job/PPRBAC/job/Openshift/job/DataSpace/job/CustomerBuilder"
                            DATASPACE_CONFIGS = './config/default.yml,./config/pprbac/commons.yml'
                            break;
                        case "SBBOL":
                            CUSTOMER_BUILDER_URL = "https://sbt-jenkins.sigma.sbrf.ru/job/SBBOL/job/DataSpace/job/CustomerBuilder"
                            DATASPACE_CONFIGS = './config/default.yml,./config/sbbol/commons.yml'
                            break;
                    }
                }
            }
        }

        stage('Detect build version') {
            steps {
                script {
                    sh 'mkdir distrib'
                    if (params.dynamicVersion) {
                        def subversion = (env.BUILD_NUMBER).toString().padLeft(4, '0')
                        VERSION = "${params.version}_${subversion}"
                    } else {
                        VERSION = params.version
                    }
                    log.info('Build version: ' + VERSION)
                }
            }
        }

        stage('Trigger CustomerBuilder') {
            when {
                expression { params.fullBuild }
            }
            steps {
                script {

                    def parameters = [
                            "isRelease" : params.release,
                            "jobMode" : "Deployer",
                            "gitModel" : GIT_LINK,
                            "branch" : params.branch,
                            "pathToConfigFile" : DATASPACE_CONFIGS,
                            "isIncludeCoreWithSearch" : true,
                            "isIncludeOnlyCore" : false,
                            "isIncludeOnlySearch" : false,
                            "isIncludeGigabas" : true,
                            "isIncludeStateMachine" : false,
                            "isIncludeDuplication" : false,
                            "buildClient" : true,
                            "needInstallToOS" : false,
                            "needQG" : params.needQG, // обязательное требование
                            "clientDeployerOptions" : params.release ? "-Dversion.forceVersion=${VERSION}" : ""
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
                            log.info("Waiting for build to queue...")
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
                            sleep(10)
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
                    def customerUrl = params.fullBuild ? CUSTOMER_DISTRIB_URL : params.customerDistrib
                    log.info("Downloading customer distrib ${customerUrl}")
                    httpRequest authentication: "${NEXUS_CREDENTIALS_ID}",
                            outputFile: "customer-distrib.zip",
                            responseHandle: 'NONE',
                            url: "${customerUrl}"
                    def dataSpaceUrl = params.fullBuild ? DATASPACE_DISTRIB_URL : params.dataspaceDistrib
                    log.info("Downloading dataspace distrib ${dataSpaceUrl}")
                    httpRequest authentication: "${NEXUS_CREDENTIALS_ID}",
                            outputFile: "dataspace-distrib.zip",
                            responseHandle: 'NONE',
                            url: "${dataSpaceUrl}"
                }
            }
        }

        stage('Prepare install_eip archive') {
            steps {
                script {
                    dir('install_eip') {
                        // extract rental property distrib
                        sh "mv ../customer-distrib.zip ./customer-distrib.zip"
                        sh "unzip customer-distrib.zip && rm customer-distrib.zip"
                        // find image hash for rental property
                        def partnersImage = sh(
                                script: "cat configs/dataspace-partners-java.yaml" +
                                        " | sed -n 's/.*partners@sha256:\\(.*\\)/\\1/p'" +
                                        " | head -1",
                                returnStdout: true).trim()
                        sh "echo \"Partners hash: ${partnersImage}\""

                        sh "sed -i 's/\${imageNameWithDigest}/partners@sha256:${partnersImage}/' Deployment/deployment-partners.yml"
                        sh "rm -r configs"
                        sh "cat Deployment/deployment-partners.yml"

                        // extract dataspace distrib
                        sh "mv ../dataspace-distrib.zip ./dataspace-distrib.zip"
                        sh "unzip dataspace-distrib.zip && rm dataspace-distrib.zip"

                        // extract version
                        def dataspaceVersion = sh(
                                script: "cat configs/dataspace-core-template.yaml" +
                                        " | sed -n 's/^.*version: \\(.*\\).*\$/\\1/p'" +
                                        " | head -1",
                                returnStdout: true).trim()
                        sh "echo \"Dataspace version: ${dataspaceVersion}\""

                        // remove version from resources names
                        sh "sed -i 's/\${MODULE_NAME}-${dataspaceVersion}/\${MODULE_NAME}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${MODULE_NAME}-${dataspaceVersion}/\${MODULE_NAME}/' configs/dataspace-gigabas-template.yaml"

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
                                artifactId: ARTIFACT_ID,
                                version: "D-${VERSION}",
                                extension: 'zip',
                                packaging: 'zip',
                                classifier: "distrib",
                                file: ARTIFACT_NAME_OS
                        )
                        log.info("Successfully published to https://nexus.sigma.sbrf.ru/nexus/content/repositories/corp-releases/ru/sberbank/pprb/sbbol/partners/partners/D-${VERSION}/")
                        log.info("Distrib url: http://nexus.sigma.sbrf.ru:8099/nexus/service/local/repositories/corp-releases/content/ru/sberbank/pprb/sbbol/partners/partners/D-${VERSION}/partners-D-${VERSION}-distrib.zip")
                        if (params.release) {
                            log.info("Publishing artifact to ${NEXUSSBRF_RELEASE_REPOSITORY}")
                            nexus.publishZip(GROUP_ID, ARTIFACT_ID, "distrib", ARTIFACT_NAME_OS, VERSION)
                            log.info("Successfully published to https://sbrf-nexus.sigma.sbrf.ru/nexus/content/repositories/Nexus_PROD/Nexus_PROD/CI02792425_sbbol-partners/partners/D-${VERSION}/partners-D-${VERSION}-distrib.zip/")
                        }
                        archiveArtifacts artifacts: "*.zip"
                    }
                }
            }
        }

        stage('Push technical flags') {
            when {
                expression { params.release }
            }
            steps {
                script {
                    dpm.publishFlags(VERSION, ARTIFACT_ID, GROUP_ID, ["bvt", "ci", "smart_regress_ift", "smart_regress_st", "smoke_ift", "smoke_st"])
                }
            }
        }
    }
    post {
        cleanup {
            cleanWs()
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
                " ${DEV_REPOSITORY}"

        response = sh(returnStdout: true, script: request)
        log.info("Response: ${response}")
        code = sh(returnStdout: true, script: "echo ${response} | grep 'http code:' ")

    }
    if (params.extension == 'flag' && params.classifier.contains('ift')) return // Костыль! Не работает публикация некоторых флагов. Мешает сборке релизов.
    def arr = code.split(":")
    if (code.trim() == '' || arr.length == 0 || arr[arr.length-1].trim() != '201') {
        error ("Failed publish to Nexus")
    }
}