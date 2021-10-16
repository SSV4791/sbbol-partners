dependencyLocking {
    lockAllConfigurations()
    ignoredDependencies.add("ru.dcbqa.allureee.*:*")
    ignoredDependencies.add("ru.dcbqa.swagger.*:*")
    lockFile.set(file("${rootDir}/gradle/dependency-locks/gradle-${project.name}.lockfile"))
}

tasks.register("resolveAndLockAll") {
    doFirst {
        require(gradle.startParameter.isWriteDependencyLocks)
    }
    doLast {
        configurations.filter {
            it.isCanBeResolved
        }.forEach { it.resolve() }
    }
}