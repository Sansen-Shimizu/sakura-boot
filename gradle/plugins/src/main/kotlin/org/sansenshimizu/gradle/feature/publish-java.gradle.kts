package org.sansenshimizu.gradle.feature

plugins { id("org.sansenshimizu.gradle.feature.publish") }

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    named("sourcesJar") { group = null }
    named("javadocJar") { group = null }
}
