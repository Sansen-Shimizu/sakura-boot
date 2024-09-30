plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The openapi module api."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(libs.spring.core)
    api(libs.swagger.annotations.jakarta)
    implementation(projects.sakuraBootCore)
    compileOnly(libs.jackson.annotations)
}
