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
        id("org.openapi.generator") version "5.2.1"
        id("org.springframework.boot") version "2.5.5"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.0"
}

gradleEnterprise {
    buildScan {
        server = "http://sbtatlas.sigma.sbrf.ru:8801"
        allowUntrustedServer = true
        isCaptureTaskInputFiles = true
        gradle.taskGraph.whenReady {
            publishAlwaysIf(!gradle.taskGraph.hasTask(":addCredentials"))
        }
    }
}

rootProject.name = "partners"

include(":docs")
include(":partners-api")
include(":partners-openapi")
include(":partners-rest")
include(":partners-service")
include(":runner")
