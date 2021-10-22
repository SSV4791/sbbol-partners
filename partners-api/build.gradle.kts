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

val apiSchemaPath = "${project(":partners-openapi").projectDir}/openapi/renters/renter.yaml"
val generateObjectOutputDir = "$buildDir/generated/sources"
openApiValidate {
    inputSpec.set(apiSchemaPath)
}

openApiGenerate {
    inputSpec.set(apiSchemaPath)
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
            "models" to "",
        )
    )
    configOptions.putAll(
        mapOf(
            "java8" to "true",
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true"
        )
    )
}

tasks {
    compileJava {
        dependsOn(
            openApiValidate,
            openApiGenerate
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
    implementation("io.swagger:swagger-annotations:1.5.23")
    implementation("javax.annotation:javax.annotation-api")
    implementation("javax.validation:validation-api")
    implementation("org.openapitools:jackson-databind-nullable:0.2.1")
    implementation("org.springframework:spring-context")
}

description = "Api ППРБ.Digital.Партнеры"
