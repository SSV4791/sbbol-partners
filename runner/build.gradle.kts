plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
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
        delete("allure-results")
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

dependencies {
    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))
    implementation(project(":partners-rest"))
    implementation(project(":partners-service"))
    implementation(project(":migration-service"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(liveLibs.liquibase.core)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.sbp.hibernate.standin)

    // реализация кэша
    runtimeOnly(liveLibs.ehcache.org.core)
    // postgres для prod-сборки
    runtimeOnly(liveLibs.postgresql.core)

    testImplementation(project(":partners-service"))

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("com.vaadin.external.google", "android-json")
    }
    testImplementation(liveLibs.mapstruct.core)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.dcb.allure.annotations)
    // заглушка для тестирования репликации между БД
    testImplementation(testLibs.orm.tests.common) {
        exclude("com.vaadin.external.google", "android-json")
    }
    testImplementation(testLibs.rest.assured)
    testImplementation(testLibs.rest.assured.common)
    testImplementation(testLibs.swagger.coverage.reporter)
}

description = "ППРБ.Digital.Партнеры"
