stage('Деплой partners') {
    withMaven(jdk: 'JDK_1.8_72_Linux', maven: 'Maven 3.5.2', mavenSettingsPath: './config/settings_alpha.xml', options: [dependenciesFingerprintPublisher(disabled: true), pipelineGraphPublisher(disabled: true)]) {
/**
 * деплой конфигов для OS
 */
        sh "mvn dependency:copy -Dartifact=ru.sberbank.pprb.sbbol.partners:partners:${env.nexus_version}:zip:distrib -Dsettings.security=./settings-security.xml -s config/settings_alpha.xml -pl ru.sberbank.pprb.sbbol.partners:partners"
        sh "mvn deploy:deploy-file -Dfile=target/dependency/partners-${env.nexus_version}-distrib.zip -Dsettings.security=./settings-security.xml -Durl=http://sbrf-nexus.ca.sbrf.ru/nexus/content/repositories/Nexus_PROD/ -DrepositoryId=distribution.repo2 -DgroupId=${env.nexus_group_id} -DartifactId=partners -Dversion=${env.nexus_version} -Dclassifier=distrib -Dpackaging=zip -s config/settings_alpha.xml -pl ru.sberbank.pprb.sbbol.partners:partners"
    }
}
