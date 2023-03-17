plugins {
    id("coverage-conventions")
    id("create-liquibase-path-conventions")
    id("dependency-locking-conventions")
    id("repositories-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("publish-release-conventions")
    id("ru.sbt.meta.meta-gradle-plugin")
}

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

    clean {
        delete("vectors")
    }

}
project
    .tasks["sonarqube"]
    .dependsOn("qaReporterUpload")

val nexusLogin = project.properties["nexusLogin"] as String?
val nexusPassword = project.properties["nexusPassword"] as String?

meta {
    nexusUrl = null
    nexusUser = nexusLogin
    nexusPassword = nexusPassword
    version = "latest.integration"
    componentId = "9655c0f1-74bf-11eb-6742-005056b72594"
    ext {
        set("url", "https://meta.sigma.sbrf.ru")
        set(
            "openApiSpecs", listOf(
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
    }
}
