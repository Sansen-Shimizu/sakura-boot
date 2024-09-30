package org.sansenshimizu.gradle.test

import org.sansenshimizu.gradle.util.DependencyUtils.getLibrary

plugins {
    java
    jacoco
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks =
        (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    testLogging.showStandardStreams = true

    maxHeapSize = "1g"
    systemProperty("file.encoding", "UTF-8")

    jvmArgs =
        listOf(
            "-javaagent:${
                configurations.getByName("byteBuddyAgent")
                    .singleFile.absolutePath
            }",
            "-Xshare:off"
        )
}

val byteBuddyAgent: Configuration =
    configurations.findByName("byteBuddyAgent")
        ?: configurations.create("byteBuddyAgent")

val libs = versionCatalogs.named("libs")

dependencies {
    testRuntimeOnly(getLibrary(libs, "junit-jupiter-engine"))
    testRuntimeOnly(getLibrary(libs, "slf4j-simple"))
    "byteBuddyAgent"(getLibrary(libs, "byte-buddy-agent"))
}

configurations.testRuntimeClasspath {
    resolutionStrategy.capabilitiesResolution {
        withCapability("org.gradlex:slf4j-impl:1.0") {
            candidates.removeIf {
                it.id.displayName.contains("org.slf4j:slf4j-simple")
            }
            select(candidates.first().id.displayName)
        }
    }
}
