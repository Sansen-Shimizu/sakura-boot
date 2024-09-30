package org.sansenshimizu.gradle.dependency

import org.sansenshimizu.gradle.util.DependencyUtils.getLibrary

plugins {
    java
    id("org.sansenshimizu.gradle.base.dependency-rules")
}

val developmentOnly: Configuration =
    configurations.findByName("developmentOnly")
        ?: configurations.create("developmentOnly")

configurations.runtimeClasspath { extendsFrom(developmentOnly) }

val libs = versionCatalogs.named("libs")

dependencies {
    compileOnly(getLibrary(libs, "jsr305"))
    developmentOnly(getLibrary(libs, "spring-boot-devtools"))
}
