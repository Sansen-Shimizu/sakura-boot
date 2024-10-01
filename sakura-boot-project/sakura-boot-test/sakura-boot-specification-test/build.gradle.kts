plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The specification test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootBasicTest)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootSpecification)
    api(projects.sakuraBootSpecificationApi)
    api(libs.junit.jupiter.api)
    api(libs.mockito.junit.jupiter)
    implementation(libs.assertj.core)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.web)
    compileOnly(libs.jackson.databind)
}
