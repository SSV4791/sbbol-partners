plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

apply(plugin = "io.spring.dependency-management")
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks {
    jar {
        enabled = false
    }
    clean {
        delete("target")
    }
    test {
        if (project.hasProperty("spring.datasource.url"))
            systemProperty("spring.datasource.url", project.properties["spring.datasource.url"] as String)
        if (project.hasProperty("spring.datasource.username"))
            systemProperty("spring.datasource.username", project.properties["spring.datasource.username"] as String)
        if (project.hasProperty("spring.datasource.password"))
            systemProperty("spring.datasource.password", project.properties["spring.datasource.password"] as String)
        if (project.hasProperty("standin.datasource.url"))
            systemProperty("standin.datasource.url", project.properties["standin.datasource.url"] as String)
        if (project.hasProperty("standin.datasource.username"))
            systemProperty("standin.datasource.username", project.properties["standin.datasource.username"] as String)
        if (project.hasProperty("standin.datasource.password"))
            systemProperty("standin.datasource.password", project.properties["standin.datasource.password"] as String)
    }
}

val pactVersion: String by rootProject

dependencies {
    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))
    implementation(project(":partners-rest"))
    implementation(project(":partners-service"))
    implementation(project(":migration-service"))

    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("sbp.integration.orm:sbp-hibernate-standin:4.1.14")
    implementation("ru.sbrf.journal:standin-client-cloud:4.0.27")
    implementation("org.liquibase:liquibase-core:4.8.0")

    // реализация кэша
    runtimeOnly("org.ehcache:ehcache:3.9.7")
    // postgres для prod-сборки
    runtimeOnly("org.postgresql:postgresql:42.3.1")

    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.rest-assured:rest-assured-common:4.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // заглушка для тестирования репликации между БД
    testImplementation("sbp.integration.orm:orm-tests-common:4.1.14")

    testImplementation("io.qameta.allure:allure-junit5:2.16.1")
    testImplementation("ru.dcbqa.allureee.annotations:dcb-allure-annotations:1.2.+")
    testImplementation("ru.dcbqa.swagger.coverage.reporter:swagger-coverage-reporter:2.3.+")
    testImplementation(group = "au.com.dius.pact.consumer", name = "junit5", version = pactVersion)
    testImplementation(group = "au.com.dius.pact.provider", name = "junit5", version = pactVersion)
}

description = "ППРБ.Digital.Партнеры"
