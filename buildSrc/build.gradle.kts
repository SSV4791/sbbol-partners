plugins {
    `kotlin-dsl`
}

repositories {
    val tokenName = project.properties["tokenName"] as String?
    val tokenPassword = project.properties["tokenPassword"] as String?
    val publicRepositoryUrl: String by project
    listOf(
        publicRepositoryUrl,
        "https://nexus-ci.delta.sbrf.ru/repository/maven-lib-int/"
    ).forEach {
        maven {
            url = uri(it)
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
    }
}

dependencies {
    implementation("com.hubspot.jinjava:jinjava:2.4.12")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.2.0")
    implementation("org.yaml:snakeyaml:1.24")
    implementation("ru.sber.dcbqa.dcb-test-plugin:gradle-plugin:4.9.3")
    implementation("ru.sbt.meta:meta-gradle-plugin:2.0.0")
    implementation("sbp.eip.metamodel:eip-metamodel-core:3.1.14-jdk11") {
        exclude("com.fasterxml.jackson.core", "jackson-databind")
    }
    implementation("sbp.eip.metamodel:eip-metamodel-scanner-gradle-plugin:3.1.14-jdk11") {
        exclude("com.fasterxml.jackson.core", "jackson-databind")
    }
}
