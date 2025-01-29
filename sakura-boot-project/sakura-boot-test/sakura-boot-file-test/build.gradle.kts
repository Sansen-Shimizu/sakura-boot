plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
            "The file test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasic)
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootFileApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(libs.jackson.databind)
    api(libs.junit.jupiter.api)
    implementation(libs.assertj.core)
    implementation(libs.commons.lang3)
    implementation(libs.hibernate.core)
    implementation(libs.jackson.core)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.test)
    implementation(libs.spring.web)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.spring.data.jpa)
}
