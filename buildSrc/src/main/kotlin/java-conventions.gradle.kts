import org.gradle.api.JavaVersion

plugins {
    `java-library`
    id("idea-conventions")
    id("repositories-conventions")
}

subprojects {
    apply(plugin = "java-library")
}

group = "ru.sberbank.pprb.sbbol.partners"

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
