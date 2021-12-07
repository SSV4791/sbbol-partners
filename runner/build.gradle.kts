import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot")
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
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
        systemProperty("file.encoding", "UTF-8")
    }
    clean {
        delete("target")
    }
}

dependencies {
    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))
    implementation(project(":partners-rest"))
    implementation(project(":partners-service"))

    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("org.springframework.boot:spring-boot-starter:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-validation:2.5.6")
    implementation("sbp.integration.orm:sbp-hibernate-standin:4.1.14")
    implementation("ru.sbrf.journal:standin-client-cloud:4.0.27")
    implementation("org.liquibase:liquibase-core:4.6.1")

    // реализация кэша
    runtimeOnly("org.ehcache:ehcache:3.9.7")
    // in-memory БД для локальных запусков
    runtimeOnly("com.h2database:h2:1.4.200")
    // postgres для prod-сборки
    runtimeOnly("org.postgresql:postgresql:42.3.1")

    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.rest-assured:rest-assured-common:4.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.6")
    // заглушка для тестирования репликации между БД
    testImplementation("sbp.integration.orm:orm-tests-common:4.1.14")
}

description = "ППРБ.Digital.Партнеры"
