pluginManagement {
    repositories {
        val tokenName: String by settings
        val tokenPassword: String by settings
        maven {
            val publicRepositoryUrl: String by settings
            url = uri(publicRepositoryUrl)
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
        maven {
            url = uri("https://nexus-ci.delta.sbrf.ru/repository/maven-lib-int/")
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
    }
    dependencyResolutionManagement {
        versionCatalogs {
            create("liveLibs") {
                from(files("gradle/libs.versions.toml"))
            }
            create("testLibs") {
                from(files("gradle/test-libs.versions.toml"))
            }
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
include(":partners-replication")
include(":partners-rest")
include(":partners-scheduler")
include(":partners-service")
include(":runner")
