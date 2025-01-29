plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
            "The file module api."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCore)
    api(libs.spring.core)
    api(libs.spring.tx)
    implementation(libs.jakarta.persistence.api)
    api(libs.hibernate.core)
}
