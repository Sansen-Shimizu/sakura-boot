plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The bulk test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootBulkApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootSpecificationApi)
    api(projects.sakuraBootSpecificationTest)
    api(libs.jackson.databind)
    api(libs.junit.jupiter.api)
    implementation(libs.assertj.core)
    implementation(libs.commons.lang3)
    implementation(libs.jackson.core)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    implementation(libs.spring.test)
    implementation(libs.spring.web)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.spring.data.jpa)
}
