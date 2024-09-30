package org.sansenshimizu.gradle.dependency

import org.sansenshimizu.gradle.util.DependencyUtils.getLibrary

plugins {
    java
    `jvm-test-suite`
    id("org.sansenshimizu.gradle.base.dependency-rules")
    id("org.sansenshimizu.gradle.test.functional-test")
}

val libs = versionCatalogs.named("libs")

dependencies {
    testCompileOnly(getLibrary(libs, "lombok"))
    testAnnotationProcessor(getLibrary(libs, "lombok"))
}
