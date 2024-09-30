plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The log module api."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies { api(libs.spring.core) }
