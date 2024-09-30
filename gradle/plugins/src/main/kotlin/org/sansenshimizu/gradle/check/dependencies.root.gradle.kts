package org.sansenshimizu.gradle.check

plugins {
    base
    id("com.autonomousapps.dependency-analysis")
}

dependencyAnalysis { issues { all { onAny { severity("fail") } } } }
