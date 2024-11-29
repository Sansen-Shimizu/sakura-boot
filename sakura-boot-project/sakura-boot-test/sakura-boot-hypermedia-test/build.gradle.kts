plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The hypermedia test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootHypermedia)
    api(projects.sakuraBootHypermediaApi)
    api(libs.junit.jupiter.api)
    api(libs.mockito.junit.jupiter)
    implementation(libs.assertj.core)
    implementation(libs.evo.inflector)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.hateoas)
    implementation(libs.spring.test)
    implementation(libs.spring.web)
    compileOnly(libs.jackson.annotations)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.jakarta.servlet.api)
}
