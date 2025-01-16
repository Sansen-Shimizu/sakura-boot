plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
        "The bulk module api."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCacheApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootHypermediaApi)
    api(projects.sakuraBootLogApi)
    api(projects.sakuraBootMapperApi)
    api(projects.sakuraBootOpenapiApi)
    api(projects.sakuraBootSpecificationApi)
    api(libs.jackson.databind)
    api(libs.jakarta.validation.api)
    api(libs.spring.core)
    api(libs.spring.tx)
    api(libs.spring.web)
    api(libs.springdoc.openapi.starter.common)
    api(libs.swagger.annotations.jakarta)
    implementation(libs.hibernate.core)
    implementation(libs.jackson.core)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.webmvc)
    runtimeOnly(libs.hibernate.validator)
}
