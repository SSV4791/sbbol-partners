tasks.register("newpatch") {
    description = "Создание sql патча"
    var currentVersion: String

    doFirst {
        if (!project.hasProperty("patchname")) {
            throw IllegalArgumentException("Property patchname not provided")
        }
        val patchName = project.property("patchname")
        if (project.hasProperty("releaseversion")) {
            currentVersion = project.property("releaseversion").toString()
        } else {
            try {
                currentVersion = project.property("version").toString().split("-")[0]
            } catch (e: Exception) {
                throw GradleException("Unable to parse version parameter value $version from gradle.properties", e)
            }
        }
        val path = "${project(":runner").projectDir}/src/main/resources/db/changelog"
        val patchDirectory = "sql/${currentVersion}"
        val patchPath = "${path}/${patchDirectory}"

        val folder = File(patchPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val patchTs = System.currentTimeMillis()

        val updateFileName = "${patchTs}_${patchName}.sql"
        val updateFilePath = "${patchPath}/${updateFileName}"

        val patchText = "-- liquibase formatted sql"
        File(updateFilePath).writeText(patchText, Charsets.UTF_8)
        println("File $updateFileName was created")

        val changelog = "changelog.yaml"
        val changelogFilePath = "${patchPath}/${changelog}"
        val mainChangeLogFilePath = "${path}/${changelog}"
        val file = File(changelogFilePath)
        var changelogText =
            """  - include:
      file: $updateFileName
      relativeToChangelogFile: true
"""
        if (file.length().toInt() == 0) {
            var mainChangelogText =
                """  - include:
      file: ${patchDirectory}/${changelog}
      relativeToChangelogFile: true
"""
            val mainChangeLogFile = File(mainChangeLogFilePath)
            if (mainChangeLogFile.length().toInt() == 0) {
                mainChangeLogFile.appendText("databaseChangeLog:\n${mainChangelogText}", Charsets.UTF_8)
            } else {
                if (!mainChangeLogFile.readText(Charsets.UTF_8).endsWith("\n")) {
                    mainChangelogText = "\n" + mainChangelogText
                }
                mainChangeLogFile.appendText(mainChangelogText, Charsets.UTF_8)
            }
            file.appendText("databaseChangeLog:\n${changelogText}", Charsets.UTF_8)
        } else {
            if (!file.readText(Charsets.UTF_8).endsWith("\n")) {
                changelogText = "\n" + changelogText
            }
            file.appendText(changelogText, Charsets.UTF_8)
        }
    }
}
