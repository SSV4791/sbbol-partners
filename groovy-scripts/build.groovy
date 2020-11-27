stage('Сборка проекта partners') {
    withMaven(jdk: 'JDK_1.8_72_Linux', maven: 'Maven 3.5.2', mavenSettingsFilePath: './config/settings_alpha.xml', options: [dependenciesFingerprintPublisher(disabled: true), pipelineGraphPublisher(disabled: true)]) {
        withEnv(['MODEL_RELEASE=anyValue']) {
            try {
                sh "rm -r model/src/main/resources/model/model"
                echo "${env.workspace}"
                sh "mvn clean install -l mvnbuild.log -P sonar -Dsettings.security=./settings-security.xml -Dmaven.repo.local=${env.workspace}/~/.dataspace_repository -s config/settings_alpha.xml"
            }
            catch (e) {
                echo "Перехвачена ошибка внутри withMaven"
                echo "Архивирование логов"
                sh label: '', script: 'zip -1 build_logs.zip mvnbuild.log'
                subject = 'сборка dataspace-core в UQGP завершена с ошибкой'
                body = """Ошибка в сборке на стадии withMaven
                    <br> Номер сборки ${env.BUILD_NUMBER}
                    <br> <a href=\"${env.BUILD_URL}\"> ССылка на сборку </a>"""
                emailext attachmentsPattern: 'build_logs.zip',
                        mimeType: 'text/html',
                        body: "${body}",
                        subject: "${subject}",
                        to: "${env.email_list}"
                throw e
            }
        }
    }
}