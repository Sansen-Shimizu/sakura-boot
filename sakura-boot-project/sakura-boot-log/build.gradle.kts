plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The log functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootLogApi)
    api(libs.aspectjweaver)
    api(libs.spring.context)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)
    implementation(libs.spring.core)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.logback.classic)
    testImplementation(libs.logback.core)
    testImplementation(libs.mockito.core)
    testCompileOnly(libs.junit.jupiter.params)
}

dependencyAnalysis {
    issues { onUnusedAnnotationProcessors { exclude(libs.lombok) } }
}
