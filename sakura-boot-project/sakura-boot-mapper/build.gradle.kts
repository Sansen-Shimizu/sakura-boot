plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The mapper functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootMapperApi)
    api(libs.aspectjweaver)
    api(libs.spring.context)
    api(libs.spring.core)
    implementation(libs.commons.lang3)
    implementation(libs.slf4j.api)
    implementation(libs.spring.data.commons)
    compileOnly(libs.mapstruct)
    compileOnly(libs.spring.data.jpa)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testCompileOnly(libs.junit.jupiter.params)
    testCompileOnly(libs.mapstruct)
    testRuntimeOnly(libs.logback.classic)
}

dependencyAnalysis {
    issues { onUnusedAnnotationProcessors { exclude(libs.lombok) } }
}
