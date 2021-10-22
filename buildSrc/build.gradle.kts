import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    `kotlin-dsl`
    id("nu.studer.credentials") version "2.1"
}

dependencyLocking {
    lockAllConfigurations()
    lockFile.set(file("../gradle/dependency-locks/gradle-${project.name}.lockfile"))
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
    implementation("ru.sbt.meta:meta-gradle-plugin:1.4.0")
}

tasks {
    register("resolveAndLockAll") {
        doFirst {
            require(gradle.startParameter.isWriteDependencyLocks)
        }
        doLast {
            configurations.filter {
                it.isCanBeResolved
            }.forEach { it.resolve() }
        }
    }
}
