plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description = "Framework to simplify the creation of a spring boot application. " +
        "The cache functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCacheApi)
    api(projects.sakuraBootCore)
    api(libs.aspectjweaver)
    api(libs.cache.api)
    api(libs.spring.beans)
    api(libs.spring.boot)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.context)
    api(libs.spring.context.support)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    implementation(libs.commons.lang3)
    implementation(libs.hibernate.core)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.hibernate.jcache)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testCompileOnly(libs.junit.jupiter.params)
    testRuntimeOnly(libs.logback.classic)
}

dependencyAnalysis {
    issues { onUnusedAnnotationProcessors { exclude(libs.lombok) } }
}
