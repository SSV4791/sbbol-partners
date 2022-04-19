import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("jacoco-conventions")
    id("java-conventions")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    systemProperty("file.encoding", "UTF-8")
}
