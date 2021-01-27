import ru.sbrf.ufs.pipeline.Const

@Library(['ufs-jobs@master']) _


pipeline {
    agent {
        label 'ufs-release'
    }
    options {
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }
    parameters {
        string(name: 'version', defaultValue: '0.0.0', description: 'Версия сборки')
        string(name: 'partnersDistrib', defaultValue: '', description: 'Ссылка на дистрибутив фабрики')
        string(name: 'dataspaceDistrib', defaultValue: '', description: 'Ссылка на дистрибутив dataspace')
        booleanParam(name: 'dynamicVersion', defaultValue: true, description: 'Добавлять номер сборки к версии (0.0.0_0022)')
        booleanParam(name: 'release', defaultValue: false, description: 'Публиковать в релизный или dev нексус')
    }
    environment {
        GIT_PROJECT = 'CIBPPRB'
        GIT_REPOSITORY = 'sbbol-partners'
        GROUP_ID = 'Nexus_PROD.CI02792425_sbbol-partners'
        ARTIFACT_ID = 'partners'
        ARTIFACT_NAME_OS = ''
        VERSION = ''
        NEXUSSBRF_RELEASE_REPOSITORY = 'https://sbrf-nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        DEV_REPOSITORY = 'https://nexus.sigma.sbrf.ru/nexus/service/local/artifact/maven/content'
        PROJECT_URL = "https://sbtatlas.sigma.sbrf.ru/stashdbo/projects/${GIT_PROJECT}/repos/${GIT_REPOSITORY}/"
    }

    stages {

        stage('Input params validation') {
            steps {
                script {
                    if (!params.version) {
                        error("Version is required")
                    } else if (!params.partnersDistrib) {
                        error("Partners distrib is required")
                    } else if (!params.dataspaceDistrib) {
                        error("Dataspace distrib is required")
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

        stage('Download distribs') {
            steps {
                script {
                    httpRequest authentication: "sbbol-nexus",
                            outputFile: "partners-distrib.zip",
                            responseHandle: 'NONE',
                            url: "${params.partnersDistrib}"
                    httpRequest authentication: "sbbol-nexus",
                            outputFile: "dataspace-distrib.zip",
                            responseHandle: 'NONE',
                            url: "${params.dataspaceDistrib}"
                }
            }
        }

        stage('Prepare install_eip archive') {
            steps {
                script {
                    dir('install_eip') {
                        // extract partners distrib
                        sh "mv ../partners-distrib.zip ./partners-distrib.zip"
                        sh "unzip partners-distrib.zip && rm partners-distrib.zip"
                        // find image hash for partners
                        def partnersImage = sh(
                                script: "cat configs/dataspace-partners-java.yaml" +
                                        " | sed -n 's/.*partners@sha256:\\(.*\\)/\\1/p'" +
                                        " | head -1",
                                returnStdout: true).trim()
                        sh "echo \"Partners hash: ${partnersImage}\""

                        sh "sed -i 's/\${REGISTY_IMAGE_HASH}/${partnersImage}/' Deployment/deployment-partners.yml"
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
                        // change module name
                        sh "sed -i 's/\${MODULE_NAME}/\${DATASPACE_MODULE_NAME}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${MODULE_NAME}/\${GIGABAS_MODULE_NAME}/' configs/dataspace-gigabas-template.yaml"
                        // customize params for fluentbit
                        sh "sed -i 's/value: dev-gen2.ca.sbrf.ru/value: \${FLUENT_BIT_CLUSTER}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/value: dev-gen2.ca.sbrf.ru/value: \${FLUENT_BIT_CLUSTER}/' configs/dataspace-gigabas-template.yaml"
                        sh "sed -i 's/value: dev/value: \${FLUENT_BIT_STAND_ID}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/value: dev/value: \${FLUENT_BIT_STAND_ID}/' configs/dataspace-gigabas-template.yaml"
                        sh "sed -i 's/value: default/value: \${FLUENT_BIT_ZONE_ID}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/value: default/value: \${FLUENT_BIT_ZONE_ID}/' configs/dataspace-gigabas-template.yaml"
                        // customize image url
                        sh "sed -i 's;\${registryUrl}/\${registryProject};\${IMAGE_BASE_URL};' configs/dataspace-core-template.yaml"
                        sh "sed -i 's;\${registryUrl}/\${registryProject};\${IMAGE_BASE_URL};' configs/dataspace-gigabas-template.yaml"
                        // customize fluentbit request/limit
                        sh "sed -i 's/memory: 32Mi/memory: \${FLUENTBIT_LIMIT_MEMORY}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/memory: 32Mi/memory: \${FLUENTBIT_LIMIT_MEMORY}/' configs/dataspace-gigabas-template.yaml"
                        sh "sed -i 's/memory: 16Mi/memory: \${FLUENTBIT_REQUEST_MEMORY}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/memory: 16Mi/memory: \${FLUENTBIT_REQUEST_MEMORY}/' configs/dataspace-gigabas-template.yaml"

                        sh "sed -i \"s/cpu: '0.2'/cpu: \\\${FLUENTBIT_LIMIT_CPU}/\" configs/dataspace-core-template.yaml"
                        sh "sed -i \"s/cpu: '0.2'/cpu: \\\${FLUENTBIT_LIMIT_CPU}/\" configs/dataspace-gigabas-template.yaml"
                        sh "sed -i \"s/cpu: '0.1'/cpu: \\\${FLUENTBIT_REQUEST_CPU}/\" configs/dataspace-core-template.yaml"
                        sh "sed -i \"s/cpu: '0.1'/cpu: \\\${FLUENTBIT_REQUEST_CPU}/\" configs/dataspace-gigabas-template.yaml"

                        sh "sed -i 's/\${{limitCPU}}/\${DATASPACE_LIMIT_CPU}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${{limitCPU}}/\${GIGABAS_LIMIT_CPU}/' configs/dataspace-gigabas-template.yaml"
                        sh "sed -i 's/\${{limitMemory}}/\${DATASPACE_LIMIT_MEMORY}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${{limitMemory}}/\${GIGABAS_LIMIT_MEMORY}/' configs/dataspace-gigabas-template.yaml"

                        sh "sed -i 's/\${{requestCPU}}/\${DATASPACE_REQUEST_CPU}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${{requestCPU}}/\${GIGABAS_REQUEST_CPU}/' configs/dataspace-gigabas-template.yaml"
                        sh "sed -i 's/\${{requestMemory}}/\${DATASPACE_REQUEST_MEMORY}/' configs/dataspace-core-template.yaml"
                        sh "sed -i 's/\${{requestMemory}}/\${GIGABAS_REQUEST_MEMORY}/' configs/dataspace-gigabas-template.yaml"

                        def dataspaceParams = """
  - description: Module name [CUSTOM]
    displayName: DATASPACE_MODULE_NAME
    name: DATASPACE_MODULE_NAME
    required: true
  - description: FluentBit cluster [CUSTOM]
    displayName: FLUENT_BIT_CLUSTER
    name: FLUENT_BIT_CLUSTER
  - description: FluentBit stand id [CUSTOM]
    displayName: FLUENT_BIT_STAND_ID
    name: FLUENT_BIT_STAND_ID
  - description: FluentBit zone id [CUSTOM]
    displayName: FLUENT_BIT_ZONE_ID
    name: FLUENT_BIT_ZONE_ID
  - description: Base image url [CUSTOM]
    displayName: IMAGE_BASE_URL
    name: IMAGE_BASE_URL
    required: true
  - description: FluentBit memory limit [CUSTOM]
    displayName: FLUENTBIT_LIMIT_MEMORY
    name: FLUENTBIT_LIMIT_MEMORY
    required: true
  - description: FluentBit memory request [CUSTOM]
    displayName: FLUENTBIT_REQUEST_MEMORY
    name: FLUENTBIT_REQUEST_MEMORY
    required: true
  - description: FluentBit cpu limit [CUSTOM]
    displayName: FLUENTBIT_LIMIT_CPU
    name: FLUENTBIT_LIMIT_CPU
    required: true
  - description: FluentBit cpu request [CUSTOM]
    displayName: FLUENTBIT_REQUEST_CPU
    name: FLUENTBIT_REQUEST_CPU
    required: true
  - description: DataSpace Core cpu limit [CUSTOM]
    displayName: DATASPACE_LIMIT_CPU
    name: DATASPACE_LIMIT_CPU
    required: true
  - description: DataSpace Core cpu request [CUSTOM]
    displayName: DATASPACE_REQUEST_CPU
    name: DATASPACE_REQUEST_CPU
    required: true
  - description: DataSpace Core memory limit [CUSTOM]
    displayName: DATASPACE_LIMIT_MEMORY
    name: DATASPACE_LIMIT_MEMORY
    required: true
  - description: DataSpace Core memory request [CUSTOM]
    displayName: DATASPACE_REQUEST_MEMORY
    name: DATASPACE_REQUEST_MEMORY
    required: true
"""


                        def gigabasParams = """
  - description: Module name [CUSTOM]
    displayName: GIGABAS_MODULE_NAME
    name: GIGABAS_MODULE_NAME
    required: true
  - description: FluentBit cluster [CUSTOM]
    displayName: FLUENT_BIT_CLUSTER
    name: FLUENT_BIT_CLUSTER
  - description: FluentBit stand id [CUSTOM]
    displayName: FLUENT_BIT_STAND_ID
    name: FLUENT_BIT_STAND_ID
  - description: FluentBit zone id [CUSTOM]
    displayName: FLUENT_BIT_ZONE_ID
    name: FLUENT_BIT_ZONE_ID
  - description: Base image url [CUSTOM]
    displayName: IMAGE_BASE_URL
    name: IMAGE_BASE_URL
    required: true
  - description: FluentBit memory limit [CUSTOM]
    displayName: FLUENTBIT_LIMIT_MEMORY
    name: FLUENTBIT_LIMIT_MEMORY
    required: true
  - description: FluentBit memory request [CUSTOM]
    displayName: FLUENTBIT_REQUEST_MEMORY
    name: FLUENTBIT_REQUEST_MEMORY
    required: true
  - description: FluentBit cpu limit [CUSTOM]
    displayName: FLUENTBIT_LIMIT_CPU
    name: FLUENTBIT_LIMIT_CPU
    required: true
  - description: FluentBit cpu request [CUSTOM]
    displayName: FLUENTBIT_REQUEST_CPU
    name: FLUENTBIT_REQUEST_CPU
    required: true
  - description: DataSpace Gigabas cpu limit [CUSTOM]
    displayName: GIGABAS_LIMIT_CPU
    name: GIGABAS_LIMIT_CPU
    required: true
  - description: DataSpace Gigabas cpu request [CUSTOM]
    displayName: GIGABAS_REQUEST_CPU
    name: GIGABAS_REQUEST_CPU
    required: true
  - description: DataSpace Gigabas memory limit [CUSTOM]
    displayName: GIGABAS_LIMIT_MEMORY
    name: GIGABAS_LIMIT_MEMORY
    required: true
  - description: DataSpace Gigabas memory request [CUSTOM]
    displayName: GIGABAS_REQUEST_MEMORY
    name: GIGABAS_REQUEST_MEMORY
    required: true
"""

                        sh "echo \"${dataspaceParams}\" >> configs/dataspace-core-template.yaml"
                        sh "echo \"${gigabasParams}\" >> configs/dataspace-gigabas-template.yaml"

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
                                credentialId: "sbbol-nexus",
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

//        stage('Push technical flags') {
//            steps {
//                script {
//                    dpm.publishFlags(VERSION, ARTIFACT_ID, GROUP_ID, ["bvt", "ci", "smart_regress_ift", "smart_regress_st", "smoke_ift", "smoke_st"])
//                }
//            }
//        }
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