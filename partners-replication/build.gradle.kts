plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    annotationProcessor(liveLibs.hibernate.jpamodelgen)

    implementation(project(":partners-adapter"))
    implementation(project(":partners-api"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation(liveLibs.hibernate.jcache)
    implementation(liveLibs.sbp.hibernate.standin)
    implementation(liveLibs.sbp.jpa.model.support)

    compileOnly(liveLibs.hibernate.jpamodelgen)
    runtimeOnly(liveLibs.ehcache.org.core)
}

description = "Replication ППРБ.Digital.Партнеры"
