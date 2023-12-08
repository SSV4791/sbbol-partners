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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
