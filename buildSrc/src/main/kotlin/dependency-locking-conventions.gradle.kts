dependencyLocking {
    lockAllConfigurations()
    ignoredDependencies.add("org.jacoco:*")
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
