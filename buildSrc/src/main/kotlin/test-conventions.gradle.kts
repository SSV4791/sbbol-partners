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

dependencies {
    testImplementation("io.rest-assured:rest-assured-common:4.4.0")
    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("org.mapstruct:mapstruct-processor:1.4.2.Final")
    testImplementation("org.mapstruct:mapstruct:1.4.2.Final")
    testImplementation("org.mock-server:mockserver-client-java:5.11.2")
    testImplementation("org.mock-server:mockserver-netty:5.11.2")
    testImplementation("org.mock-server:mockserver-spring-test-listener:5.11.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.6")
    testImplementation("uk.co.jemos.podam:podam:7.2.5.RELEASE")
}
