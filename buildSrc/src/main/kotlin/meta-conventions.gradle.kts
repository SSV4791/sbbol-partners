import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    id("nu.studer.credentials")
    id("ru.sbt.meta.meta-gradle-plugin")
}

val credentials: CredentialsContainer by project.extra
val nexusLoginValue = (project.properties["nexusLogin"] ?: credentials.getProperty("nexusLogin")) as String?
val nexusPasswordValue = (project.properties["nexusPassword"] ?: credentials.getProperty("nexusPassword")) as String?

meta {
    nexusUrl = null
    nexusUser = nexusLoginValue
    nexusPassword = nexusPasswordValue
    componentId = "9655c0f1-74bf-11eb-6742-005056b72594"
    ext {
        set("url", "https://meta.sigma.sbrf.ru")
        set(
            "openApiSpecs",
            listOf(
                //Renter
                "${project(":partners-openapi").projectDir}/openapi/renters/renter.yaml",
                //Partner
                "${project(":partners-openapi").projectDir}/openapi/partners/account.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/account_sign.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/contact.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/contact_address.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/contact_document.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/contact_email.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/contact_phone.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/dictionary_budget_mask.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/dictionary_document.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/partner.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/partner_address.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/partner_document.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/partner_email.yaml",
                "${project(":partners-openapi").projectDir}/openapi/partners/partner_phone.yaml",
                //Counterparty
                "${project(":partners-openapi").projectDir}/openapi/counterparties/counterparty.yaml",
            )
        )
        set("analyzeJava", false)
        set("failBuildOnError", true)
    }
}
