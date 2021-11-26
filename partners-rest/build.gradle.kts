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
val partnersApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/partners/partner.yaml"
val partnersAccountsApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/partners/partner_account.yaml"
val counterpartiesApiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/counterparties/counterparty.yaml"
val generateObjectOutputDir = "$buildDir/generated/sources"
openApiValidate {
    inputSpec.set(rentersApiSchemaPath)
}
openApiValidate {
    inputSpec.set(partnersApiSchemaPath)
}
openApiValidate {
    inputSpec.set(partnersAccountsApiSchemaPath)
}
openApiValidate {
    inputSpec.set(counterpartiesApiSchemaPath)
}

tasks {
    register("openApiGenerateRenters", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set(rentersApiSchemaPath)
        outputDir.set(generateObjectOutputDir)
        generatorName.set("spring")
        generateAliasAsModel.set(false)
        generateApiTests.set(false)
        generateApiDocumentation.set(false)
        generateModelTests.set(false)
        modelPackage.set("ru.sberbank.pprb.sbbol.partners.renter.model")
        apiPackage.set("ru.sberbank.pprb.sbbol.partners.renter")
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
    register("openApiGeneratePartners", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set(partnersApiSchemaPath)
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
    register("openApiGeneratePartnersAccounts", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        inputSpec.set(partnersAccountsApiSchemaPath)
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

    compileJava {
        dependsOn(
            openApiValidate,
            "openApiGenerateRenters",
            "openApiGeneratePartners",
            "openApiGeneratePartnersAccounts",
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
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.6")
}

description = "REST Controllers ППРБ.Digital.Партнеры"
