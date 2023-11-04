plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.springframework.boot") apply false
    id("test-conventions")
}

dependencies {
    implementation(project(":partners-api"))
    implementation(project(":partners-replication"))
    implementation(project(":partners-service"))

    implementation(platform(liveLibs.spring.boot.dependencies))
    implementation(liveLibs.shedlock.provider.jdbc.template)
    implementation(liveLibs.shedlock.spring)
    implementation(liveLibs.spring.boot.starter)
}

description = "Scheduler ППРБ.Digital.Партнеры"
