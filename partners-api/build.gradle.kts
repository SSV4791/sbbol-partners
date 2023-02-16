plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
}

val rentersApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/renters/renter.yaml"
val partnersApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/partners"
val counterpartiesApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/counterparties/counterparty.yaml"
val generateObjectOutputDir = "$buildDir/generated/sources"

tasks {
    register("openApiGenerateRenters", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set(rentersApiSchemaPath)
        outputDir.set(generateObjectOutputDir)
        generatorName.set("spring")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.renter.model")
        globalProperties.putAll(
            mapOf(
                "models" to "",
            )
        )
        configOptions.putAll(
            mapOf(
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "skipDefaultInterface" to "true"
            )
        )
    }
    register("openApiGenerateCounterparties", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set(counterpartiesApiSchemaPath)
        outputDir.set(generateObjectOutputDir)
        generatorName.set("spring")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.counterparties.model")
        globalProperties.putAll(
            mapOf(
                "models" to "",
            )
        )
        configOptions.putAll(
            mapOf(
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "serializableModel" to "true",
                "skipDefaultInterface" to "true",
                "useTags" to "true"
            )
        )
    }
    val openApiGeneratePartners =
        file(partnersApiSchemaPath).listFiles()
            ?.filter { it.extension in listOf("yml", "yaml") }
            ?.mapIndexed { idx, file ->
                register("openApiGenerate$idx", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
                    inputSpec.set(file.absolutePath)
                    outputDir.set(generateObjectOutputDir)
                    generatorName.set("spring")
                    generateAliasAsModel.set(false)
                    generateApiTests.set(false)
                    generateApiDocumentation.set(false)
                    generateModelTests.set(false)
                    templateDir.set("${project(":partners-api").projectDir}/config/openapi-generator/templates/JavaSpring")
                    modelPackage.set("ru.sberbank.pprb.sbbol.partners.model")
                    globalProperties.putAll(
                        mapOf(
                            "models" to "",
                        )
                    )
                    configOptions.putAll(
                        mapOf(
                            "dateLibrary" to "java8",
                            "interfaceOnly" to "true",
                            "serializableModel" to "true",
                            "skipDefaultInterface" to "true",
                            "useTags" to "true"
                        )
                    )
                }
            }

    compileJava {
        if (!openApiGeneratePartners.isNullOrEmpty())
            dependsOn(openApiGeneratePartners)
        dependsOn(
            "openApiGenerateRenters",
            "openApiGenerateCounterparties",

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
    implementation(liveLibs.apache.commons.lang3)
    implementation(liveLibs.aspectjrt)
    implementation(liveLibs.jackson.databind.nullable)
    implementation(liveLibs.javax.annotation.api)
    implementation(liveLibs.javax.validation.api)
    implementation(liveLibs.slf4j.api)
    implementation(liveLibs.spring.context.core)
    implementation(liveLibs.swagger.annotations.core)
}

description = "Api ППРБ.Digital.Партнеры"
