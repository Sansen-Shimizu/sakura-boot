plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
            "The cache test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootCacheApi)
    api(libs.junit.jupiter.api)
    api(libs.mockito.junit.jupiter)
    implementation(libs.assertj.core)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    compileOnly(libs.jackson.core)
    compileOnly(libs.spring.context)
}
