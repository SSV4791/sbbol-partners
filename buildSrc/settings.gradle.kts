pluginManagement {
    repositories {
        maven {
            val publicRepositoryUrl: String by settings
            url = uri(publicRepositoryUrl)
            isAllowInsecureProtocol = true
            val tokenName: String by settings
            val tokenPassword: String by settings
            credentials {
                username = tokenName
                password = tokenPassword
            }
        }
    }
}
