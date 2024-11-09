package org.sansenshimizu.gradle.feature

plugins {
    id("org.antora")
    id("com.github.node-gradle.node")
}

antora { packages.put("@antora/lunr-extension", "latest") }

tasks.named("antora") {
    group = "documentation"
    finalizedBy("addStaticIndex")
}

tasks.register<Copy>("addStaticIndex") {
    from(file("$projectDir/index.html"))
    into(layout.buildDirectory)
}
