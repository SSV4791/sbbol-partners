import org.gradle.kotlin.dsl.jacoco

plugins {
    jacoco
}

apply(plugin = "jacoco")

jacoco {
    toolVersion = "0.8.7"
}
