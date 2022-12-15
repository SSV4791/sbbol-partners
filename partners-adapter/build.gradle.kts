plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
    id("test-conventions")
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
    implementation(liveLibs.antifraud.api)
    implementation(liveLibs.antifraud.rpc.api)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(liveLibs.ehcache.net.core)
    implementation(liveLibs.jackson.databind)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.jsonrpc4j.client)
    implementation(liveLibs.jsr305)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.spring.context.support)
    implementation(liveLibs.swagger.annotations.core)
}

description = "Adapter ППРБ.Digital.Партнеры"
