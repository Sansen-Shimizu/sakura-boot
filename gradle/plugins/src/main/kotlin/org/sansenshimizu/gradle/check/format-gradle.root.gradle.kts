package org.sansenshimizu.gradle.check

plugins {
    id("com.diffplug.spotless")
    id("org.sansenshimizu.gradle.base.lifecycle")
}

repositories.mavenCentral()

spotless {
    kotlinGradle {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(80) }
        target("gradle/plugins/src/main/**/*.gradle.kts")
    }
    kotlin {
        ktfmt().kotlinlangStyle().configure { it.setMaxWidth(80) }
        target("gradle/plugins/src/main/**/*.kt")
    }
}

tasks {
    named("qualityCheck") { dependsOn(tasks.spotlessCheck) }
    named("qualityGate") { dependsOn(tasks.spotlessApply) }
}
