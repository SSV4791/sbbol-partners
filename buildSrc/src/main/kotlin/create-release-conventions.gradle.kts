tasks.register<Zip>("fullDistrib") {
    destinationDirectory.set(file("${rootProject.buildDir}"))
    archiveFileName.set("${rootProject.name}.zip")
    from("$rootDir/openshift/") {
        into("openshift")
        exclude("Deployment*")
    }
    from("$rootDir/openshift/") {
        into("openshift")
        include("Deployment*")
        filter { line: String -> line.replace("{app_docker_image}", project.properties["dockerImage"] as String) }
    }
    from("$rootDir/docs") {
        into("docs")
    }
    from("${project(":runner").projectDir}/src/main/resources/db/changelog/") {
        into("liquibase")
    }
    from("${project(":partners-adapter").projectDir}/src/main/resources/audit/") {
        into("package/conf/data/audit_metamodel/")
    }
    from("${project(":runner").projectDir}/vectors/") {
        into("vectors")
    }
}
