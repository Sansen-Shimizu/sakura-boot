package org.sansenshimizu.gradle.test

plugins {
    java
    `jvm-test-suite`
    id("org.sansenshimizu.gradle.test.test")
    id("org.sansenshimizu.gradle.test.integration-test")
    id("org.sansenshimizu.gradle.test.functional-test")
    id("org.sansenshimizu.gradle.test.performance-test")
}

tasks.create("allTest") {
    group = "verification"
    dependsOn(testing.suites.named("test"))
    dependsOn(testing.suites.named("integrationTest"))
    dependsOn(testing.suites.named("functionalTest"))
    dependsOn(testing.suites.named("performanceTest"))
}
