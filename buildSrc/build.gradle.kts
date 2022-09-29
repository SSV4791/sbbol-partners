plugins {
    `kotlin-dsl`
}

val tokenName = project.properties["tokenName"] as String?
val tokenPassword = project.properties["tokenPassword"] as String?

repositories {
    maven {
        val publicRepositoryUrl: String by project
        url = uri(publicRepositoryUrl)
        credentials {
            username = tokenName
            password = tokenPassword
        }
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://nexus-ci.delta.sbrf.ru/repository/maven-proxy-lib-internal/")
        credentials {
            username = tokenName
            password = tokenPassword
        }
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation("ru.sbt.meta:meta-gradle-plugin:1.5.0") {
        exclude("org.glassfish.ha", "ha-api")
    }
}
