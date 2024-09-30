package org.sansenshimizu.gradle.util

import java.io.File
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

object ProjectUtils {

    fun Settings.includeDir(depth: Int = 1) {

        rootDir
            .walk()
            .maxDepth(depth)
            .onEnter { it.isDirectory }
            .filter {
                !(it.parentFile.name == "gradle" && it.name == "plugins") &&
                    File(it, "build.gradle.kts").exists() &&
                    rootDir != it
            }
            .map { it.toRelativeString(rootDir) }
            .forEach {
                if (it.contains(File.separatorChar)) {
                    val folder = it.substringBeforeLast(File.separatorChar)
                    val module = it.substringAfterLast(File.separatorChar)
                    include(":$module")
                    project(":$module").projectDir = File("$folder/$module")
                } else {
                    include(it)
                }
            }
    }

    fun Project.aggregateDir(projectName: String, depth: Int = 1) {

        rootDir
            .listFiles()
            ?.first { it.name == projectName }
            ?.walk()
            ?.maxDepth(depth)
            ?.onEnter { it.isDirectory && rootDir != it }
            ?.filter {
                it.parentFile.name != "gradle" &&
                    File(it, "build.gradle.kts").exists() &&
                    rootDir != it
            }
            ?.map { it.toRelativeString(rootDir) }
            ?.forEach {
                val dependencyHandler = project.dependencies
                if (it.contains(File.separatorChar)) {
                    val module = it.substringAfterLast(File.separatorChar)
                    dependencyHandler.add("implementation", project(":$module"))
                } else {
                    dependencyHandler.add("implementation", project(":$it"))
                }
            }
    }
}
