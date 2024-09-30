package org.sansenshimizu.gradle.dependency

import org.sansenshimizu.gradle.util.DependencyUtils.getLibrary

plugins {
    java
    id("org.sansenshimizu.gradle.base.dependency-rules")
}

val libs = versionCatalogs.named("libs")

dependencies { testCompileOnly(getLibrary(libs, "jsr305")) }
