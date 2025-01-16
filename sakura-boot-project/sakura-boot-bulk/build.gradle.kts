plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The bulk functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootBulkApi)
    api(projects.sakuraBootCore)
    api(libs.aspectjweaver)
    api(libs.jakarta.persistence.api)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.tx)
    implementation(libs.slf4j.api)
    implementation(libs.spring.beans)
    compileOnly(libs.spring.data.jpa)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito.core)
    testRuntimeOnly(libs.logback.classic)
    testRuntimeOnly(libs.spring.data.jpa)
}

dependencyAnalysis {
    issues { onUnusedAnnotationProcessors { exclude(libs.lombok) } }
}
