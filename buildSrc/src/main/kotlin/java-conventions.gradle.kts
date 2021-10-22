import org.gradle.api.JavaVersion

plugins {
    `java-library`
    id("repositories-conventions")
}

group = "ru.sberbank.pprb.sbbol.partners"
version = "DEV-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
