plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    annotationProcessor(liveLibs.mapstruct.processor)
    annotationProcessor(liveLibs.hibernate.jpamodelgen)

    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))
    implementation(project(":partners-replication"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation(liveLibs.antifraud.api)
    implementation(liveLibs.bundles.micrometer)
    implementation(liveLibs.hibernate.jcache)
    implementation(liveLibs.javax.servlet.api)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.sbp.hibernate.standin)
    //Поддержка генераторов ID на время перехода.
    implementation(liveLibs.sbp.jpa.model.support)

    compileOnly(liveLibs.hibernate.jpamodelgen)
    runtimeOnly(liveLibs.ehcache.org.core)
}

description = "Service ППРБ.Digital.Партнеры"
