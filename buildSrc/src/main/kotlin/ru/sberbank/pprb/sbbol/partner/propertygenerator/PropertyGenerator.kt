package ru.sberbank.pprb.sbbol.partner.propertygenerator

import com.hubspot.jinjava.Jinjava
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Генератор файла настроек (*.properties) из шаблона *.properties.j2 и файлов значений параметров
 */
class PropertyGenerator(
    private val defaultPropertiesFile: File,
    private val customPropertiesFile: File?,
    private val templateDir: File,
    private val outputDir: File) {

    fun run() {
        val outputDirectoryLocal = outputDir
        outputDirectoryLocal.mkdirs()
        val defaultProperties = readProperties(defaultPropertiesFile)
        val customProperties = readProperties(customPropertiesFile)
        val mergedProperties = mergeProperties(defaultProperties, customProperties)

        templateDir.walk()
            .filter { it.isFile }
            .forEach { templateFile ->
                val propertiesFileContent = Jinjava().render(templateFile.readText(), mergedProperties)
                val filename = templateFile.name.substringBeforeLast(".j2")
                val outputFile = File(outputDirectoryLocal, filename)
                outputFile.writeText(propertiesFileContent)
            }
    }

    private fun readProperties(propertiesFile: File?): Map<String, *> {
        fun mapProps(sourceMap: Map<*, *>): Map<String, *> =
            sourceMap.entries
                .associate {
                    (it.key as String) to when (it.value) {
                        null -> ""
                        is Map<*, *> -> mapProps(it.value as Map<*, *>)
                        else -> it.value.toString()
                    }
                }
        return if (propertiesFile == null || !propertiesFile.exists())
            mapOf<String, String>()
        else
            mapProps(propertiesFile.reader()
                .use { reader -> Yaml().loadAs(reader, Map::class.java) }
            )
    }

    private fun mergeProperties(default: Map<String, *>, custom: Map<String, *>): Map<String, *> {
        fun reduce(defaultParam: Any?, customParam: Any?): Any? =
            when {
                defaultParam is Map<*, *> && customParam is Map<*, *> ->
                    @Suppress("UNCHECKED_CAST")
                    mergeProperties(defaultParam as Map<String, *>, customParam as Map<String, *>)
                defaultParam is List<*> && customParam is List<*> ->
                    defaultParam.toMutableSet()
                        .apply { addAll(customParam) }
                        .toList()
                else ->
                    customParam
            }

        return default.toMutableMap().apply {
            custom.forEach { merge(it.key, it.value ?: "") { a, b -> reduce(a, b) } }
        }
    }
}
