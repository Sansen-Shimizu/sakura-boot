plugins {
    alias(libs.plugins.component.application)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

dependencies {
    developmentOnly(libs.spring.boot.docker.compose)
    implementation(projects.sakuraBootStarterPredefinedBasic)

    functionalTestImplementation(projects.sakuraBootStarterPredefinedBasicFunctionalTest)
    functionalTestCompileOnly(libs.lombok)
    functionalTestAnnotationProcessor(libs.lombok)

    integrationTestImplementation(projects.sakuraBootStarterPredefinedBasicIntegrationTest)
    integrationTestCompileOnly(libs.lombok)
    integrationTestAnnotationProcessor(libs.lombok)

    testImplementation(projects.sakuraBootStarterBasicUnitTest)
}

dependencyAnalysis {
    issues {
        onUsedTransitiveDependencies { severity("ignore") }
        onUnusedAnnotationProcessors { exclude(libs.lombok) }
    }
}
