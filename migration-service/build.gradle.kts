plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)
    annotationProcessor(liveLibs.hibernate.jpamodelgen)

    implementation(project(":partners-api"))
    implementation(project(":partners-service"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    // решает ошибку при старте приложения: java.lang.ClassNotFoundException: javax.jws.WebParam
    implementation(liveLibs.jaxws) {
        // решает ошибку: Could not find ha-api-3.1.12.hk2-jar (org.glassfish.ha:ha-api:3.1.12)
        exclude("org.glassfish.ha", "ha-api")
    }
    implementation(liveLibs.jsonrpc4j.client)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.sbp.hibernate.standin)
    implementation(liveLibs.spring.boot.starter.data.jpa)

    compileOnly(liveLibs.hibernate.jpamodelgen)
}

description = "Сервис для миграции данных из Legacy СББОЛ"
