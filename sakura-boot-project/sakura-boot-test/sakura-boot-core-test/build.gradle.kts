plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The core test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootLogApi)
    api(libs.aspectjweaver)
    api(libs.junit.jupiter.api)
    api(libs.junit.jupiter.params)
    api(libs.mockito.junit.jupiter)
    api(libs.spring.core)
    api(libs.spring.test)
    implementation(libs.assertj.core)
    implementation(libs.commons.lang3)
    implementation(libs.mockito.core)
    implementation(libs.slf4j.api)
    compileOnly(libs.logback.classic)
    compileOnly(libs.logback.core)
    compileOnly(libs.spring.data.jpa)
    compileOnly(libs.spring.tx)
}
