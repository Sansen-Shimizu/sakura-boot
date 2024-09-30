plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The mapper test functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootMapper)
    api(projects.sakuraBootMapperApi)
    api(libs.junit.jupiter.api)
    api(libs.mockito.junit.jupiter)
    implementation(projects.sakuraBootCore)
    implementation(libs.assertj.core)
    implementation(libs.mockito.core)
    implementation(libs.spring.core)
    compileOnly(libs.mapstruct)
    compileOnly(libs.spring.data.jpa)
}
