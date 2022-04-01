plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

apply(plugin = "io.spring.dependency-management")
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks {
    clean {
        delete("target")
    }
}

val pactVersion: String by rootProject

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("net.sf.ehcache:ehcache:2.10.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-context-support:4.3.8.RELEASE")

    testImplementation("io.qameta.allure:allure-junit5:2.16.1")
    testImplementation("ru.dcbqa.allureee.annotations:dcb-allure-annotations:1.2.+")
    testImplementation("ru.dcbqa.swagger.coverage.reporter:swagger-coverage-reporter:2.3.+")
    testImplementation(group = "au.com.dius.pact.consumer", name = "junit5", version = pactVersion)
    testImplementation(group = "au.com.dius.pact.provider", name = "junit5", version = pactVersion)
}

description = "Adapter ППРБ.Digital.Партнеры"
