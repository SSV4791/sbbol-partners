plugins {
    jacoco
    id("dependency-locking-conventions")
    id("java-conventions")
    id("meta-conventions")
    id("org.sonarqube") version "3.3"
    id("ru.sbrf.build.gradle.qa.reporter") version "2.0.0"
}

jacoco {
    toolVersion = "0.8.7"
}

tasks {

    val allTestsCoverageFile = "${rootProject.buildDir}/coverage/jacoco/rootTestsCoverage.exec"
    val allTestsCoverageReportXml = "${rootProject.buildDir}/coverage/jacoco/report/report.xml"
    val allTestsCoverageReportHtml = "${rootProject.buildDir}/coverage/jacoco/report/html"

    // Таска берет .exec файлы по проекту и мержит в один
    val jacocoMergeTest by registering(JacocoMerge::class) {
        dependsOn(subprojects.map { it.tasks.test })
        destinationFile = file(allTestsCoverageFile)
        executionData = project.fileTree(rootDir) {
            include("**/build/jacoco/test.exec")
        }
    }

    // таска на основе общего exec файла генерирует отчет
    val jacocoRootReport by registering(JacocoReport::class) {
        group = "verification"
        dependsOn(jacocoMergeTest)
        reports {
            xml.required.set(true)
            xml.outputLocation.set(file(allTestsCoverageReportXml))
            html.required.set(true)
            html.outputLocation.set(file(allTestsCoverageReportHtml))
        }
        additionalSourceDirs.setFrom(files(subprojects.map { it.sourceSets["main"].allSource.srcDirs }.flatten()))
        sourceDirectories.setFrom(files(subprojects.map { it.sourceSets["main"].allSource.srcDirs }.flatten()))
        classDirectories.setFrom(files(subprojects.map { it.sourceSets["main"].output }.flatten()))
        executionData.setFrom(files(allTestsCoverageFile))

        //Фильтры для путей, которые не попадут в общий Jacoco отчет.
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "ru.sberbank.pprb.sbbol.partners.renter.model",
                    "ru.sberbank.pprb.sbbol.partners.renter.entity",
                )
            }
        }))
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
        property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.buildDir}/coverage/jacoco/report/report.xml")
        property(
            "sonar.coverage.exclusions", """
            **/partners-api/**/*,
            **/partners-service/**/*,
            **/runner/**/*
        """.trimIndent()
        )
        property(
            "sonar.cpd.exclusions", """
                    src/main/java/ru/sberbank/pprb/sbbol/partners/entity/**,
                """.trimIndent()
        )

    }
}

project.tasks["sonarqube"].dependsOn("jacocoRootReport")
