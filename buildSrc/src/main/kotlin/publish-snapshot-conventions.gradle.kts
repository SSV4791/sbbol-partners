import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
    id("create-release-conventions")
}

tasks.register("snapshot") {
    group = "publishing"
    description = "Publish snapshot distributions to nexus CI snapshot repository"
    dependsOn("publishSnapshotBuildPublicationToSnapshotBuildRepository")
}

publishing {
    repositories {
        val credentials: CredentialsContainer by project.extra
        val nexusLogin = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
        val nexusPassword = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?

        maven {
            val snapshotReleaseRepositoryUrl: String by project
            url = uri(snapshotReleaseRepositoryUrl)

            name = "SnapshotBuild"
            credentials {
                username = nexusLogin
                password = nexusPassword
            }
        }
    }
    publications {
        register<MavenPublication>("SnapshotBuild") {

            groupId = "ru.sbrf.ufs.sbbol"

            artifactId = "partners"
            artifact(tasks["fullDistrib"]) {
                classifier = "distrib.configs"
            }
        }
    }
}

