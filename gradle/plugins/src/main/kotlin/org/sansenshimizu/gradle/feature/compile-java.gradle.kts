package org.sansenshimizu.gradle.feature

plugins {
    java
    id("org.sansenshimizu.gradle.base.lifecycle")
}

val jdkVersion: String by project

java.toolchain.languageVersion = JavaLanguageVersion.of(jdkVersion)

tasks {
    withType<JavaCompile>().configureEach {
        options.apply {
            isFork = true
            encoding = "UTF-8"
            compilerArgs.add("-parameters")
            compilerArgs.add("-implicit:none")
            compilerArgs.add("-Werror")
            compilerArgs.add("-Xlint:all,-serial,-processing")
        }
    }
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        filePermissions { unix("0664") }
        dirPermissions { unix("0775") }
    }
    named("qualityCheck") { dependsOn(tasks.withType<JavaCompile>()) }
    named("qualityGate") { dependsOn(tasks.withType<JavaCompile>()) }
    buildDependents { setGroup(null) }
    buildNeeded { setGroup(null) }
    jar { setGroup(null) }
}

sourceSets.all { tasks.named(classesTaskName) { group = null } }
