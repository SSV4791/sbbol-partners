plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)

    implementation(project(":partners-service"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // решает ошибку при старте приложения: java.lang.ClassNotFoundException: javax.jws.WebParam
    implementation(liveLibs.jaxws) {
        // решает ошибку: Could not find ha-api-3.1.12.hk2-jar (org.glassfish.ha:ha-api:3.1.12)
        exclude("org.glassfish.ha", "ha-api")
    }
    implementation(liveLibs.jsonrpc4j.client)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.sbp.hibernate.standin)
}

description = "Сервис для миграции данных из Legacy СББОЛ"
