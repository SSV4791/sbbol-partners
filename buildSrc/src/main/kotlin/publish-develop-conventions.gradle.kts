import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
    id("create-release-conventions")
}

tasks.register("develop") {
    group = "publishing"
    description = "Publish develop distributions to nexus CI release repository"
    dependsOn("publishDevelopBuildPublicationToDevelopBuildRepository")
}

publishing {
    repositories {
        val credentials: CredentialsContainer by project.extra
        val nexusLogin = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
        val nexusPassword = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?


        maven {
            val devReleaseRepositoryUrl: String by project
            url = uri(devReleaseRepositoryUrl)

            name = "DevelopBuild"
            credentials {
                username = nexusLogin
                password = nexusPassword
            }
        }
    }
    publications {
        register<MavenPublication>("DevelopBuild") {
            groupId = "ru.sbrf.ufs.sbbol"
            artifactId = "partners"
            artifact(tasks["fullDistrib"]) {
                classifier = "distrib.configs"
            }
        }
    }
}

