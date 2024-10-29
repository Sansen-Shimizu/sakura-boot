package org.sansenshimizu.gradle.feature

import com.github.gradle.node.npm.task.NpxTask

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
