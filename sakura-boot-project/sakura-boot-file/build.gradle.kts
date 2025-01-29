plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
            "The file functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootFileApi)
    api(libs.spring.core)
    implementation(libs.jakarta.persistence.api)
}
