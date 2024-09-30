package org.sansenshimizu.gradle.component

import io.fuchs.gradle.collisiondetector.DetectCollisionsTask

plugins {
    application
    id("org.sansenshimizu.gradle.base.dependency-rules")
    id("org.sansenshimizu.gradle.base.lifecycle")
    id("org.sansenshimizu.gradle.check.dependencies")
    id("org.sansenshimizu.gradle.check.format-gradle")
    id("org.sansenshimizu.gradle.check.format-java")
    id("org.sansenshimizu.gradle.feature.spring-boot-application")
    id("org.sansenshimizu.gradle.feature.compile-java")
    id("org.sansenshimizu.gradle.feature.javadoc")
    id("org.sansenshimizu.gradle.test.all-test")
}

tasks.named<DetectCollisionsTask>("detectCollisions").configure {
    collisionFilter { exclude("**.html", "**.txt", "LICENSE") }
}
