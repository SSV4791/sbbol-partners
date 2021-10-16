plugins {
    id("nu.studer.credentials")
}

repositories {
    val credentials: nu.studer.gradle.credentials.domain.CredentialsContainer by project.extra
    val nexusLogin = project.findProperty("nexusLogin") as String?
        ?: credentials.getProperty("nexusLogin") as String?
    val nexusPassword = project.findProperty("nexusPassword") as String?
        ?: credentials.getProperty("nexusPassword") as String?

    maven {
        val publicRepositoryUrl: String by project
        url = uri(publicRepositoryUrl)
    }

    listOf(
        "https://nexus.sigma.sbrf.ru/nexus/content/groups/internal"
    ).forEach {
        maven {
            url = uri(it)
            credentials {
                username = nexusLogin
                password = nexusPassword
            }
        }
    }
}
