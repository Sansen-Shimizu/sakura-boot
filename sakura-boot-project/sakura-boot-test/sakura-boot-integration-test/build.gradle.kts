plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The integration test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootBulkApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootSpecificationApi)
    api(libs.jackson.core)
    api(libs.jackson.databind)
    api(libs.junit.jupiter.api)
    api(libs.spring.test)
    api(libs.spring.tx)
    implementation(libs.assertj.core)
    implementation(libs.evo.inflector)
    implementation(libs.json.path)
    implementation(libs.mockito.core)
    implementation(libs.spring.boot.test)
    implementation(libs.spring.core)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.web)
    compileOnly(libs.hamcrest)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.spring.data.jpa)
}
