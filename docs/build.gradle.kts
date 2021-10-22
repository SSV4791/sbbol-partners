plugins {
    id("dependency-locking-conventions")
    id("java-conventions")
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("repositories-conventions")
}

tasks {
    val buildAdminGuideDocs by registering(org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
        setSourceDir(file("asciidoc/admin-guide"))
        setOutputDir(file("${buildDir}/docs/admin-guide"))
        baseDirFollowsSourceDir()
        sources(delegateClosureOf<PatternSet> {
            include("index.adoc")
        })
    }

    build {
        dependsOn(buildAdminGuideDocs)
    }
}

description = "ППРБ. Фабика Партнеры модуль формирования документации."
