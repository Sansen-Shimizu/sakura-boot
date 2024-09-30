plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
}

description = "Framework to simplify the creation of a spring boot application. " +
        "The core functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(libs.aspectjweaver)
    api(libs.jackson.annotations)
    api(libs.jakarta.validation.api)
    api(libs.slf4j.api)
    api(libs.spring.beans)
    api(libs.spring.boot)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    api(libs.spring.data.jpa)
    api(libs.spring.tx)
    api(libs.spring.web)
    api(libs.spring.webmvc)
    implementation(projects.sakuraBootLogApi)
    implementation(libs.commons.lang3)
    implementation(libs.hibernate.core)
    implementation(libs.spring.context)
    implementation(libs.spring.expression)
    implementation(libs.spring.jcl)
    compileOnly(libs.jakarta.servlet.api)
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.assertj.core)
    testImplementation(libs.jakarta.servlet.api)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.test)
}
