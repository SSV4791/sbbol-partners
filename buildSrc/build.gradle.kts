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
    implementation("com.hubspot.jinjava:jinjava:2.4.12")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.2.0")
    implementation("org.yaml:snakeyaml:1.24")
    implementation("ru.sbrf.build.gradle.dcb-test-plugin:gradle-plugin:4.1.3")
    implementation("ru.sbt.meta:meta-gradle-plugin:2.0.0")
}
