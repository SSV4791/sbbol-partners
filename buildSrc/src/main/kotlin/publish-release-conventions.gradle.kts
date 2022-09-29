import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
    id("create-release-conventions")
}

publishing {
    repositories {
        val tokenName = project.properties["tokenName"] as String?
        val tokenPassword = project.properties["tokenPassword"] as String?

        maven {
            name = "publish"
            url = uri(project.properties["releaseRepositoryUrl"]!!)
            isAllowInsecureProtocol = true
            credentials {
                username = tokenName
                password = tokenPassword
            }
        }
    }
    publications {
        register<MavenPublication>("publish") {
            groupId = "${project.properties["groupId"]}"
            artifactId = "${project.properties["artifactId"]}"
            artifact(tasks["fullDistrib"]) {
                classifier = "distrib"
            }
        }
    }
}

