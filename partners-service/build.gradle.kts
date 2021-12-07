import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
}

apply(plugin = "io.spring.dependency-management")
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks {
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
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")

    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))

    implementation("io.micrometer:micrometer-core:1.7.5")
    implementation("io.micrometer:micrometer-registry-jmx:1.7.5")
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.5")
    implementation("org.hibernate:hibernate-jcache:5.6.1.Final")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.6")
    implementation("sbp.integration.orm:sbp-hibernate-standin:4.1.14")
    //Поддержка генераторов ID на время перехода.
    implementation("sbp.com.sbt.dataspace:jpa-model-support:4.3.34")

    runtimeOnly("org.ehcache:ehcache:3.9.7")
    runtimeOnly("org.aspectj:aspectjweaver:1.9.7")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.6")
    testImplementation("uk.co.jemos.podam:podam:7.2.7.RELEASE")
}

description = "Service ППРБ.Digital.Партнеры"
