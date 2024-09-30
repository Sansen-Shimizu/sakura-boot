plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The cache module api."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootLogApi)
    api(libs.spring.context)
    api(libs.spring.core)
}
