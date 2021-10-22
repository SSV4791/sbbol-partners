import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    id("nu.studer.credentials")
    id("ru.sbt.meta.meta-gradle-plugin")
}

val credentials: CredentialsContainer by project.extra
val nexusLoginValue = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
val nexusPasswordValue = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?

meta {
    nexusUrl = null
    nexusUser = nexusLoginValue
    nexusPassword = nexusPasswordValue
    componentId = "APP744"
    ext {
        set("url", "https://meta.sigma.sbrf.ru")
        set("openApiSpecs", listOf("/partners-openapi/openapi/renters/renter.yaml"))
        set("analyzeJava", false)
        set("failBuildOnError", true)
    }
}
