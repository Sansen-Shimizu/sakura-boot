package org.sansenshimizu.gradle.feature

import com.github.gradle.node.npm.task.NpxTask
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins { id("com.github.node-gradle.node") }

tasks.register<NpxTask>("antoraUIBundle") {
    group = "documentation"
    command.set("gulp")
    args.set(listOf("bundle"))
}

tasks.register<NpxTask>("antoraUIBundleKeepPreview") {
    group = "documentation"
    command.set("gulp")
    args.set(listOf("bundle:pack"))
}

tasks.register<NpxTask>("antoraUIPreview") {
    group = "documentation"
    command.set("gulp")
    args.set(listOf("preview:build"))
}

tasks.register<Exec>("antoraUIOpenPreview") {
    val outputFile = file("public/index.html")
    if (outputFile.exists()) {
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        when {
            os.isWindows -> exec {
                commandLine("cmd", "/c", "start", outputFile.absolutePath)
            }

            os.isMacOsX -> exec {
                commandLine("open", outputFile.absolutePath)
            }

            os.isLinux -> exec {
                commandLine("xdg-open", outputFile.absolutePath)
            }

            else -> throw UnsupportedOperationException("Unsupported OS: ${os.name}")
        }
    } else {
        logger.warn("File not found: ${outputFile.absolutePath}")
    }
}

tasks.register("antoraUIPreviewAndOpen") {
    group = "documentation"
    dependsOn(tasks.named("antoraUIPreview"))
    dependsOn(tasks.named("antoraUIOpenPreview"))
}
