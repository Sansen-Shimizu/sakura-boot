plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The mapper module api."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootLogApi)
    api(libs.spring.core)
    compileOnly(libs.mapstruct)
    compileOnly(libs.spring.data.jpa)
}
