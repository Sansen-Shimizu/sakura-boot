plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description = "Framework to simplify the creation of a spring boot application. " +
        "The specification functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasic)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootLogApi)
    api(projects.sakuraBootSpecificationApi)
    api(libs.aspectjweaver)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    api(libs.spring.data.jpa)
    implementation(projects.sakuraBootLog)
    implementation(libs.commons.lang3)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.slf4j.api)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testRuntimeOnly(libs.logback.classic)
}

dependencyAnalysis {
    issues { onUnusedAnnotationProcessors { exclude(libs.lombok) } }
}
