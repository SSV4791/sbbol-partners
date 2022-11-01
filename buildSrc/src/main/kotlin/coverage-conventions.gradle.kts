plugins {
    id("org.sonarqube")
    id("ru.sbrf.build.gradle.dcb-test-plugin")
}

val coverageExclusions = listOf(
    // Классы с конфигурациями
    "ru/sbrf/ufs/sbbol/Application*",
    //POJO
    "**/model/**",
    "**/enums/**",
    "**/entity/**",
    //Классы с контроллерами и вызовами сервисов без логики, в которых происходит только вызов соответствующего сервиса
    "**/*Controller*",
    "**/*Adapter*",
    "**/*Api*",
    "**/*Client*",
    //Классы с заглушками для локальной разработки
    "**/*Stub*",
    //Сериализаторы/десериализаторы
    "**/handler/*Handler*",
    //Классы с exception
    "**/exception/**",
    //Инфраструктура
    "**/swagger/**",
    "**/*Aspect*",
    "**/*Config*"
)

sonarqube {
    val sonarToken = project.properties["sonarToken"] as String?
    properties {
        property("sonar.projectKey", "ru.sberbank.pprb.sbbol.partners:partners")
        property("sonar.host.url", "https://sbt-sonarqube.sigma.sbrf.ru")
        property("sonar.login", "$sonarToken")
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.buildDir}/coverage/jacoco/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", coverageExclusions)
        property(
            "sonar.sources", """
            "openapi",
            "src/main/java",
            "src/main/resources"
        """.trimIndent()
        )
        property(
            "sonar.cpd.exclusions", """
                    partners-service/src/main/java/ru/sberbank/pprb/sbbol/partners/entity/**,
                """.trimIndent()
        )
    }
}

dcbTestPlugin{
    jacocoCoverage{
        excludePackages.addAll(coverageExclusions)
    }
}
