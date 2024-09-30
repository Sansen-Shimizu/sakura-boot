package org.sansenshimizu.gradle.base

plugins { base }

tasks {
    register("qualityCheck") {
        group = "build"
        description = "Runs checks (without executing tests)"
    }
    register("qualityGate") {
        group = "build"
        description = "Runs checks and autocorrects (without executing tests)"
    }
    check { dependsOn(tasks.named("qualityCheck")) }
}
