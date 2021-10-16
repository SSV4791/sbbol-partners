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
        }
    }
    clean {
        delete("target")
    }
}

dependencies {
    implementation(project(":partners-api"))
    implementation(project(":partners-rest"))
    implementation(project(":partners-service"))

    implementation("net.logstash.logback:logstash-logback-encoder:6.3")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("sbp.integration.orm:sbp-hibernate-standin:4.1.14")
    implementation("ru.sbrf.journal:standin-client-cloud:4.0.27")
    implementation("org.liquibase:liquibase-core")

    // реализация кэша
    runtimeOnly("org.ehcache:ehcache:3.9.2")
    // in-memory БД для локальных запусков
    runtimeOnly("com.h2database:h2:1.4.200")
    // postgres для prod-сборки
    runtimeOnly("org.postgresql:postgresql:42.2.19")

    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:rest-assured-common")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // заглушка для тестирования репликации между БД
    testImplementation("sbp.integration.orm:orm-tests-common:4.1.14")
}

description = "ППРБ.Digital.Партнеры"
