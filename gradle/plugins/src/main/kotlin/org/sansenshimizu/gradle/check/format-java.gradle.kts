package org.sansenshimizu.gradle.check

plugins {
    id("com.diffplug.spotless")
    id("org.sansenshimizu.gradle.base.lifecycle")
}

spotless.java {
    eclipse()
        .configFile(
            "$rootDir/sakura-boot-checkstyle/formatter/java-formatter.xml"
        )
}

tasks {
    named("qualityCheck") { dependsOn(tasks.spotlessCheck) }
    named("qualityGate") { dependsOn(tasks.spotlessApply) }
}
