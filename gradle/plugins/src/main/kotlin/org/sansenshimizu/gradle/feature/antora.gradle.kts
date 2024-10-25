package org.sansenshimizu.gradle.feature

plugins {
    id("org.antora")
    id("com.github.node-gradle.node")
}

tasks.named("antora") {
    group = "documentation"
}
