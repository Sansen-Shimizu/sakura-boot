package org.sansenshimizu.gradle.task

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class PluginApplicationOrderAnalysis : DefaultTask() {

    @get:InputFiles abstract val pluginSrcFolders: ConfigurableFileCollection

    @get:OutputFile abstract val pluginApplicationDiagram: RegularFileProperty

    @TaskAction
    fun analyse() {
        val pluginDependencies =
            pluginSrcFolders
                .flatMap { srcFolder ->
                    findGradleFiles(srcFolder).map { it to srcFolder }
                }
                .groupBy { (file, srcFolder) ->
                    extractPackagePath(file, srcFolder)
                }
                .mapValues { (_, files) ->
                    files.associate { (pluginFile, srcFolder) ->
                        val pluginId =
                            extractFullPluginName(pluginFile, srcFolder)
                        pluginId to extractPluginIds(pluginFile.readText())
                    }
                }
                .filter { it.value.isNotEmpty() }

        val lineBreak = "\n            "
        pluginApplicationDiagram
            .get()
            .asFile
            .writeText(
                """
            @startuml

            ${
                    pluginDependencies.map { (packagePath, pluginIds) ->
                        "package \"$packagePath\" {$lineBreak" +
                                pluginIds.keys.joinToString("") {
                                    "agent \"$it\"$lineBreak"
                                } +
                                "$lineBreak}"
                    }.joinToString(lineBreak)
                }

            ${
                    pluginDependencies.values.flatMap { it.entries }
                        .filter { it.value.isNotEmpty() }
                        .flatMap { (from, to) ->
                            to.map {
                                "\"$from\" --down--> \"$it\"$lineBreak"
                            }
                        }
                        .joinToString(lineBreak)
                }
               
            @enduml
        """
                    .trimIndent()
            )

        logger.lifecycle(
            "Diagram: ${pluginApplicationDiagram.get().asFile.absolutePath}"
        )
    }

    private fun extractPackagePath(file: File, srcFolder: File): String {
        val relativePath = file.relativeTo(srcFolder).parentFile
        return relativePath.path.replace(File.separatorChar, '.')
    }

    private fun findGradleFiles(directory: File): List<File> {
        return directory
            .walkTopDown()
            .filter { it.isFile && it.name.endsWith(".gradle.kts") }
            .toList()
    }

    private fun extractPluginIds(script: String): List<String> {
        return pluginsBlock(script)
            .lines()
            .filter { it.contains("id(") }
            .map { it.substring(it.indexOf("id(\"") + 4, it.indexOf("\")")) }
    }

    private fun pluginsBlock(script: String): String {
        return script.indexOf("}").let {
            when (it) {
                -1 -> ""
                else -> script.substring(0, it)
            }
        }
    }

    private fun extractFullPluginName(
        pluginFile: File,
        srcFolder: File
    ): String {
        val packagePath = extractPackagePath(pluginFile, srcFolder)
        val pluginName = pluginFile.name.replaceFirst(".gradle.kts", "")
        return "$packagePath.$pluginName"
    }
}
