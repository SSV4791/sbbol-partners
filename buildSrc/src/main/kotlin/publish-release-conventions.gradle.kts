import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
    id("create-release-conventions")
}

tasks.register("release") {
    group = "publishing"
    description = "Publish release distributions to nexus CDP release repository"
    dependsOn("publishReleaseBuildPublicationToReleaseBuildRepository")
}

publishing {
    repositories {
        val credentials: CredentialsContainer by project.extra
        val nexusLogin = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
        val nexusPassword = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?

        maven {
            val releaseRepositoryUrl: String by project
            url = uri(releaseRepositoryUrl)

            name = "ReleaseBuild"
            credentials {
                username = nexusLogin
                password = nexusPassword
            }
        }
    }
    publications {
        register<MavenPublication>("ReleaseBuild") {
            groupId = "Nexus_PROD.CI02792425_sbbol-partners"

            artifactId = "partners"
            artifact(tasks["fullDistrib"]) {
                classifier = "distrib.configs"
            }
        }
    }
}

