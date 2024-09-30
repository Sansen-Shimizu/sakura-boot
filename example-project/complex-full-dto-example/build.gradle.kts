plugins {
    alias(libs.plugins.component.application)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

dependencies {
    developmentOnly(libs.spring.boot.docker.compose)
    implementation(projects.sakuraBootStarterPredefinedAllModule)
    annotationProcessor(libs.hibernate.jpamodelgen)
    annotationProcessor(libs.mapstruct.processor)

    functionalTestImplementation(projects.sakuraBootStarterPredefinedAllModuleFunctionalTest)
    functionalTestCompileOnly(libs.lombok)
    functionalTestAnnotationProcessor(libs.lombok)

    integrationTestImplementation(projects.sakuraBootStarterPredefinedAllModuleIntegrationTest)
    integrationTestCompileOnly(libs.lombok)
    integrationTestAnnotationProcessor(libs.lombok)

    testImplementation(projects.sakuraBootStarterAllModuleUnitTest)
}

dependencyAnalysis {
    issues {
        onUsedTransitiveDependencies { severity("ignore") }
        onUnusedAnnotationProcessors { exclude(libs.lombok) }
    }
}
