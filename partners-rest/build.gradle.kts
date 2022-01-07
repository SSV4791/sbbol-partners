plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.openapi.generator")
    id("org.springframework.boot") apply false
}

apply(plugin = "io.spring.dependency-management")
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
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
        apiPackage.set("ru.sberbank.pprb.sbbol.renter")
        globalProperties.putAll(
            mapOf(
                "apis" to "",
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
        apiPackage.set("ru.sberbank.pprb.sbbol.counterparties")
        globalProperties.putAll(
            mapOf(
                "apis" to "",
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
                    modelPackage.set("ru.sberbank.pprb.sbbol.partners.model")
                    apiPackage.set("ru.sberbank.pprb.sbbol.partners")
                    globalProperties.putAll(
                        mapOf(
                            "apis" to "",
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
            "openApiGenerateCounterparties"
        )
    }
    clean {
        delete("target")
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
    implementation(project(":partners-api"))
    implementation(project(":partners-service"))

    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.6")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.6")
}

description = "REST Controllers ППРБ.Digital.Партнеры"
