plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
}

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The openapi functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(libs.spring.boot)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.springdoc.openapi.starter.common)
    implementation(projects.sakuraBootHypermediaApi)
    implementation(projects.sakuraBootOpenapiApi)
    implementation(libs.spring.data.commons)
    implementation(libs.spring.hateoas)
    implementation(libs.spring.web)
    implementation(libs.swagger.core.jakarta)
    implementation(libs.swagger.models.jakarta)
    annotationProcessor(libs.spring.boot.configuration.processor)
}
