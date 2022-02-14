plugins {
    id("create-liquibase-path-conventions")
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("meta-conventions")
    id("org.sonarqube") version "3.3"
    id("publish-develop-conventions")
    id("publish-release-conventions")
    id("publish-snapshot-conventions")
    id("ru.sbrf.build.gradle.qa.reporter") version "2.0.0"
}

tasks {

    val coverageFile = "${rootProject.buildDir}/jacoco/test.exec"
    val coverageReportXml = "${rootProject.buildDir}/jacoco/report/report.xml"
    val coverageReportHtml = "${rootProject.buildDir}/jacoco/report/html"

    // таска на основе общего exec файла генерирует отчет
    val jacocoRootReport by registering(JacocoReport::class) {
        group = "verification"
        reports {
            xml.required.set(true)
            xml.outputLocation.set(file(coverageReportXml))
            html.required.set(true)
            html.outputLocation.set(file(coverageReportHtml))
        }
        sourceDirectories.setFrom(sourceSets["main"].allSource.srcDirs)
        classDirectories.setFrom(sourceSets["main"].output)
        executionData.setFrom(files(coverageFile))
    }

    val sonarqube by getting {
        dependsOn(jacocoRootReport)
    }

    clean {
        delete(buildDir)
    }
}

sonarqube {
    val credentials: nu.studer.gradle.credentials.domain.CredentialsContainer by project.extra
    val sonarToken = (project.properties["sonarToken"] ?: credentials.getProperty("sonarToken")) as String?

    properties {
        property("sonar.projectKey", "ru.sberbank.pprb.sbbol.partners:partners")
        property("sonar.host.url", "https://sbt-sonarqube.sigma.sbrf.ru")
        property("sonar.login", "$sonarToken")
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.buildDir}/jacoco/report/report.xml")
        property(
            "sonar.coverage.exclusions", """
            **/partners-api/**/*,
        """.trimIndent()
        )
        property(
            "sonar.cpd.exclusions", """
                    src/main/java/ru/sberbank/pprb/sbbol/partners/entity/**,
                """.trimIndent()
        )

    }
}
