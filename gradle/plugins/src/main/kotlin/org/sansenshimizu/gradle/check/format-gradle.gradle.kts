package org.sansenshimizu.gradle.check

import org.sansenshimizu.gradle.spotless.SortDependenciesStep

plugins {
    id("com.diffplug.spotless")
    id("org.sansenshimizu.gradle.base.lifecycle")
}

repositories.mavenCentral()

spotless.kotlinGradle {
    ktfmt().kotlinlangStyle().configure { it.setMaxWidth(80) }
    addStep(SortDependenciesStep.create())
}

tasks {
    named("qualityCheck") { dependsOn(tasks.spotlessCheck) }
    named("qualityGate") { dependsOn(tasks.spotlessApply) }
}
