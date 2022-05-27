plugins {
    id("create-liquibase-path-conventions")
    id("dependency-locking-conventions")
    id("io.qameta.allure") version "2.9.6"
    id("jacoco-conventions")
    id("java-conventions")
    id("meta-conventions")
    id("org.sonarqube") version "3.2.0"
    id("publish-develop-conventions")
    id("publish-release-conventions")
    id("publish-snapshot-conventions")
    id("ru.sbrf.build.gradle.qa.reporter") version "3.3.+"
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

tasks {

    qaReporterUpload {
        jacocoExcludes.addAll(coverageExclusions)
    }

    register("sonarCoverage", DefaultTask::class) {
        group = "verification"
        dependsOn(jacocoTestReport)
        finalizedBy(sonarqube)
    }
}

sonarqube {
    val credentials: nu.studer.gradle.credentials.domain.CredentialsContainer by project.extra
    val sonarToken = (project.properties["sonarToken"] ?: credentials.getProperty("sonarToken")) as String?

    properties {
        property("sonar.projectKey", "ru.sberbank.pprb.sbbol.partners:partners")
        property("sonar.host.url", "https://sbt-sonarqube.sigma.sbrf.ru")
        property("sonar.login", "$sonarToken")
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.buildDir}/coverage/jacoco/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", coverageExclusions)
        property(
            "sonar.cpd.exclusions", """
                    partners-service/src/main/java/ru/sberbank/pprb/sbbol/partners/entity/**,
                """.trimIndent()
        )
    }
}

qaReporter {
    projectKey.set("sbbol-partners")
}
