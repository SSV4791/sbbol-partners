plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

apply(plugin = "io.spring.dependency-management")
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks {
    clean {
        delete("target")
    }
}

dependencies {
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")

    implementation("com.github.briandilley.jsonrpc4j:jsonrpc4j:1.6")
    // решает ошибку при старте приложения: java.lang.ClassNotFoundException: javax.jws.WebParam
    implementation("com.sun.xml.ws:jaxws-rt:2.3.3") {
        // решает ошибку: Could not find ha-api-3.1.12.hk2-jar (org.glassfish.ha:ha-api:3.1.12)
        exclude("org.glassfish.ha", "ha-api")
    }
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("sbp.integration.orm:sbp-hibernate-standin:4.1.14")
    //Поддержка генераторов ID на время перехода.
    implementation("sbp.com.sbt.dataspace:jpa-model-support:4.3.34")

}

description = "Сервис для миграции данных из Legacy СББОЛ"
