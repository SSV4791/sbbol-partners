plugins {
    id("create-liquibase-path-conventions")
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("org.sonarqube") version "3.2.0"
    id("publish-develop-conventions")
    id("publish-release-conventions")
    id("publish-snapshot-conventions")
    id("ru.sbrf.build.gradle.qa.reporter") version "3.3.4"
    id("ru.sbt.meta.meta-gradle-plugin")
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

    register("jacocoRootReport", JacocoReport::class) {
        dependsOn(subprojects.map { it.tasks.test })
        group = "verification"

        val coverageFile = "${rootProject.buildDir}/jacoco/test.exec"
        additionalSourceDirs.setFrom(files(subprojects.map { it.sourceSets["main"].allSource.srcDirs }.flatten()))
        sourceDirectories.setFrom(files(subprojects.map { it.sourceSets["main"].allSource.srcDirs }.flatten()))
        classDirectories.setFrom(files(subprojects.map { it.sourceSets["main"].output }.flatten()))
        executionData.setFrom(files(rootProject.fileTree(rootDir) {
            include(coverageFile)
        }))
    }

    register("sonarCoverage", DefaultTask::class) {
        group = "verification"
        dependsOn("jacocoRootReport")
        finalizedBy(qaReporterUpload)
        finalizedBy(sonarqube)
    }

    test {
        dependsOn(subprojects.flatMap { it.tasks.withType<Test>() })
    }

    qaReporterUpload {
        jacocoExcludes.addAll(coverageExclusions)
    }
}
project
    .tasks["sonarqube"]
    .dependsOn("qaReporterUpload")

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


meta {
    val credentials: nu.studer.gradle.credentials.domain.CredentialsContainer by project.extra
    val nexusLoginValue = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
    val nexusPasswordValue =
        (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?
    nexusUrl = "https://nexus.sigma.sbrf.ru/nexus/content/repositories/internal"
    nexusUser = nexusLoginValue
    nexusPassword = nexusPasswordValue
    version = "latest.release"
    componentId = "9655c0f1-74bf-11eb-6742-005056b72594"
    ext {
        set("url", "https://meta.sigma.sbrf.ru")
        set(
            "openApiSpecs", mutableListOf(
                "partners-openapi/openapi/renters/renter.yaml",
                "partners-openapi/openapi/partners/account.yaml",
                "partners-openapi/openapi/partners/account_sign.yaml",
                "partners-openapi/openapi/partners/contact.yaml",
                "partners-openapi/openapi/partners/contact_address.yaml",
                "partners-openapi/openapi/partners/contact_document.yaml",
                "partners-openapi/openapi/partners/contact_email.yaml",
                "partners-openapi/openapi/partners/contact_phone.yaml",
                "partners-openapi/openapi/partners/dictionary_budget_mask.yaml",
                "partners-openapi/openapi/partners/dictionary_document.yaml",
                "partners-openapi/openapi/partners/partner.yaml",
                "partners-openapi/openapi/partners/partner_address.yaml",
                "partners-openapi/openapi/partners/partner_document.yaml",
                "partners-openapi/openapi/partners/partner_email.yaml",
                "partners-openapi/openapi/partners/partner_phone.yaml",
                "partners-openapi/openapi/counterparties/counterparty.yaml",
            )
        )
        set("analyzeJava", false)
        set("failBuildOnError", true)
    }
}
