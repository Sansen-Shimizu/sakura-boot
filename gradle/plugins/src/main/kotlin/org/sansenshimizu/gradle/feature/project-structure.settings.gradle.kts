package org.sansenshimizu.gradle.feature

import org.sansenshimizu.gradle.util.ProjectUtils.includeDir

dependencyResolutionManagement.repositories.mavenCentral()

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeDir(3)
