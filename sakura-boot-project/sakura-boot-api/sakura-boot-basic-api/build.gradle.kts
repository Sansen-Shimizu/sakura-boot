plugins { alias(libs.plugins.component.framework) }

description = "Framework to simplify the creation of a spring boot application. " +
        "The basic module api."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCacheApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootHypermediaApi)
    api(projects.sakuraBootLogApi)
    api(projects.sakuraBootMapperApi)
    api(projects.sakuraBootOpenapiApi)
    api(libs.jackson.databind)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    api(libs.spring.data.jpa)
    api(libs.spring.tx)
    api(libs.spring.web)
    api(libs.springdoc.openapi.starter.common)
    api(libs.swagger.annotations.jakarta)
    implementation(libs.jackson.core)
    implementation(libs.spring.webmvc)
}
