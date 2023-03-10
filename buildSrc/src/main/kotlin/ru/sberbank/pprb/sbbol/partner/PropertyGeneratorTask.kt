package ru.sberbank.pprb.sbbol.partner

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import ru.sberbank.pprb.sbbol.partner.propertygenerator.PropertyGenerator
import java.io.File

/**
 * Генератор файла настроек (*.properties) из шаблона *.properties.j2 и файлов значений параметров
 */
@CacheableTask
open class PropertyGeneratorTask : DefaultTask() {

    /**
     * Путь к каталогу с шаблонами файлов настроек
     */
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    var templateDir: File? = null

    /**
     * Путь к файлу значений параметров по умолчанию
     */
    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    var defaultPropertiesFile: File? = null

    /**
     * Путь к файлу значений переопределенных параметров
     */
    @Optional
    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    var customPropertiesFile: File? = null

    /**
     * Путь к каталогу сохранения сгенерированных файлов настроек
     */
    @OutputDirectory
    var outputDir: File? = null

    @TaskAction
    fun run() {
        PropertyGenerator(defaultPropertiesFile!!, customPropertiesFile, templateDir!!, outputDir!!).run()
    }
}
