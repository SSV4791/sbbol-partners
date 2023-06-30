import ru.sberbank.pprb.sbbol.partner.PropertyGeneratorTask

plugins {
    id("dependency-locking-conventions")
    id("jacoco-conventions")
    id("java-conventions")
    id("org.springframework.boot")
    id("test-conventions")
}

val defaultTestPropertiesDirPath = "${projectDir}/src/test/resources/application"
val customDirPath = "${rootDir}/custom"
val propertiesTestFileName = "applicationProps.yml"
tasks {
    val generateTestConfig by register<PropertyGeneratorTask>("generateTestConfig") {
        val custom = File("${customDirPath}/application/test/${propertiesTestFileName}")
        if (custom.exists())
            customPropertiesFile = custom

        defaultPropertiesFile = file("${defaultTestPropertiesDirPath}/test/${propertiesTestFileName}")
        templateDir = fileTree("${rootDir}/config/application/test").dir
        outputDir = sourceSets["test"].output.resourcesDir as File
    }
    register<Test>("generateVectorTest") {
        useJUnitPlatform()
        filter { includeTestsMatching("**changevector.generate.*") }
    }
    register<Test>("applyVectorTest") {
        useJUnitPlatform()
        filter { includeTestsMatching("**changevector.apply.*") }
    }
    register<Copy>("createCustom") {
        from(defaultTestPropertiesDirPath)
        into("${customDirPath}/application")
    }
    jar {
        enabled = false
    }
    clean {
        delete("allure-results")
        delete("vectors")
    }
    build {
        dependsOn(generateTestConfig)
    }
    test {
        dependsOn(generateTestConfig)
        useJUnitPlatform()
        exclude("**/changevector/**")
    }
}

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))

    implementation(project(":migration-service"))
    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))
    implementation(project(":partners-rest"))
    implementation(project(":partners-scheduler"))
    implementation(project(":partners-service"))

    implementation(liveLibs.hibernate.jcache)
    implementation(liveLibs.http.healthcheck.starter)
    implementation(liveLibs.liquibase.core)
    implementation(liveLibs.logstash.logback.encoder)
    implementation(liveLibs.sbp.hibernate.standin)
    implementation(liveLibs.spring.boot.starter)
    implementation(liveLibs.spring.boot.starter.data.jpa)
    implementation(liveLibs.spring.boot.starter.validation)
    implementation(liveLibs.spring.boot.starter.web)
    // реализация кэша
    runtimeOnly(liveLibs.ehcache.org.core)
    // postgres для prod-сборки
    runtimeOnly(liveLibs.postgresql.core)

    testImplementation(project(":partners-replication"))
    testImplementation(project(":partners-service"))

    testImplementation(liveLibs.antifraud.api)
    testImplementation(liveLibs.aspectjrt)
    testImplementation(liveLibs.mapstruct.core)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.bundles.rest.assured)
    // заглушка для тестирования репликации между БД
    testImplementation(testLibs.orm.tests.common) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
    testImplementation(testLibs.postgresql)
    testImplementation(testLibs.spring.boot.starter.test) {
        exclude("com.h2database", "h2")
        exclude("com.vaadin.external.google", "android-json")
    }
    testImplementation(liveLibs.spring.context.support)
    testImplementation(liveLibs.spring.retry)
}

description = "ППРБ.Digital.Партнеры"
