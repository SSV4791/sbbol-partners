pluginManagement {
    repositories {
        maven {
            val publicRepositoryUrl: String by settings
            url = uri(publicRepositoryUrl)
        }
        maven {
            url = uri("https://nexus.sigma.sbrf.ru/nexus/content/repositories/thirdparty")
        }
    }
    plugins {
        id("org.openapi.generator") version "5.3.1"
        id("org.springframework.boot") version "2.5.5"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.0"
}

gradleEnterprise {
    buildScan {
        server = "http://dev-sbbol2.sigma.sbrf.ru:8801"
        allowUntrustedServer = true
        isCaptureTaskInputFiles = true
        gradle.taskGraph.whenReady {
            publishAlwaysIf(!gradle.taskGraph.hasTask(":addCredentials"))
        }
    }
}

rootProject.name = "sbbol-partners"

include(":docs")
include(":migration-service")
include(":partners-adapter")
include(":partners-api")
include(":partners-openapi")
include(":partners-rest")
include(":partners-service")
include(":runner")
