plugins {
    id("dependency-locking-conventions")
    id("eip-metamodel-scanner")
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
    implementation(liveLibs.antifraud.api)
    implementation(liveLibs.bundles.micrometer)
    implementation(liveLibs.hibernate.jcache)
    implementation(liveLibs.javax.servlet.api)
    implementation(liveLibs.mapstruct.core)
    implementation(liveLibs.sbp.hibernate.standin)
    //Поддержка генераторов ID на время перехода.
    implementation(liveLibs.sbp.jpa.model.support)
    implementation(liveLibs.spring.boot.starter.aop)
    implementation(liveLibs.spring.boot.starter.data.jpa)

    compileOnly(liveLibs.hibernate.jpamodelgen)
    runtimeOnly(liveLibs.ehcache.org.core)
}

val componentName = "${project.properties["metaComponentName"]}"
metamodel {
    componentCode = componentName
    isFormattedOutput = true
}

modelJpa {
    fileName = "$componentName.ldm.xml"
    modelName = componentName
    modelVersion = "${project.version}"
    packages = "ru.sberbank.pprb.sbbol.partners.entity"
}

description = "Service ППРБ.Digital.Партнеры"
