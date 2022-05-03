plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

apply(plugin = "jacoco")
apply(plugin = "ru.sbrf.build.gradle.qa.reporter")

tasks {
    clean {
        delete("target")
    }
}

val generateObjectOutputDir = "$buildDir/generated/sources"

tasks {
    register("openApiGenerateAudit", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set("${project(":partners-openapi").projectDir}/openapi/audit/audit-api.yaml")
        outputDir.set(generateObjectOutputDir)
        generatorName.set("java")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.audit.model")
        apiPackage.set("ru.sberbank.pprb.sbbol.audit.api")
        invokerPackage.set("ru.sberbank.pprb.sbbol.audit.invoker")
        library.set("resttemplate")
        configOptions.set(
            mapOf(
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "skipDefaultInterface" to "true"
            )
        )
    }
    compileJava {
        dependsOn(
            "openApiGenerateAudit"
        )
    }
}

sourceSets {
    main {
        java {
            srcDir("${generateObjectOutputDir}/src/main/java")
        }
    }
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(liveLibs.ehcache.net.core)
    implementation(liveLibs.jackson.databind)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.jsr305)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.spring.context.support)
    implementation(liveLibs.swagger.annotations.core)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(testLibs.bundles.mockserver)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.dcb.allure.annotations)
    testImplementation(testLibs.junit5.allure)
    testImplementation(testLibs.podam.core)
    testImplementation(testLibs.swagger.coverage.reporter)
}

description = "Adapter ППРБ.Digital.Партнеры"
