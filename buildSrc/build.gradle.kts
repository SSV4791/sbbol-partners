import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    `kotlin-dsl`
    id("nu.studer.credentials") version "2.1"
}

val credentials: CredentialsContainer by project.extra
val nexusLoginValue = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
val nexusPasswordValue = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?

repositories {
    maven {
        val publicRepositoryUrl: String by project
        url = uri(publicRepositoryUrl)
    }
    maven {
        url = uri("https://nexus.sigma.sbrf.ru/nexus/content/groups/internal/")
        credentials {
            username = nexusLoginValue
            password = nexusPasswordValue
        }
    }
}

dependencies {
    implementation("nu.studer:gradle-credentials-plugin:2.1")
    implementation("ru.sbt.meta:meta-gradle-plugin:1.5.0") {
        exclude("org.glassfish.ha", "ha-api")
    }
}
