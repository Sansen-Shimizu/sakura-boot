plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The hypermedia module api."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootLogApi)
    api(libs.spring.core)
    api(libs.spring.hateoas)
    implementation(projects.sakuraBootCore)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.web)
    compileOnly(libs.jackson.annotations)
}
