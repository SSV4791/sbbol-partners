plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
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

dependencies {
    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(liveLibs.ehcache.net.core)
    implementation(liveLibs.jackson.databind)
    implementation(liveLibs.spring.context.support)

    testImplementation(testLibs.bundles.mockserver)
    testImplementation(testLibs.bundles.pact)
    testImplementation(testLibs.dcb.allure.annotations)
    testImplementation(testLibs.junit5.allure)
    testImplementation(testLibs.podam.core)
    testImplementation(testLibs.swagger.coverage.reporter)
}

description = "Adapter ППРБ.Digital.Партнеры"
