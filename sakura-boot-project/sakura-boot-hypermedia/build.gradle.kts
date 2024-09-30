plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description = "Framework to simplify the creation of a spring boot application. " +
        "The hypermedia functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootHypermediaApi)
    api(libs.aspectjweaver)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.hateoas)
    implementation(projects.sakuraBootMapperApi)
    implementation(libs.commons.lang3)
    implementation(libs.jackson.annotations)
    implementation(libs.slf4j.api)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.web)
    compileOnly(libs.jackson.databind)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.jakarta.servlet.api)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.spring.test)
    testCompileOnly(libs.jackson.databind)
    testCompileOnly(libs.junit.jupiter.params)
    testRuntimeOnly(libs.logback.classic)
}

dependencyAnalysis {
    issues {
        onAny { exclude(libs.jackson.databind) }
        onUnusedAnnotationProcessors { exclude(libs.lombok) }
    }
}
