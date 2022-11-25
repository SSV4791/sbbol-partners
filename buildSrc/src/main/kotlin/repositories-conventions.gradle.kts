repositories {
    val tokenName = project.properties["tokenName"] as String?
    val tokenPassword = project.properties["tokenPassword"] as String?

    val publicRepositoryUrl: String by project
    listOf(
        publicRepositoryUrl,
        "https://nexus-ci.delta.sbrf.ru/repository/maven-proxy-lib-internal/",
        "https://nexus-ci.delta.sbrf.ru/repository/maven-lib-release/"

    ).forEach {
        maven {
            url = uri(it)
            credentials {
                username = tokenName
                password = tokenPassword
            }
            isAllowInsecureProtocol = true
        }
    }
}
