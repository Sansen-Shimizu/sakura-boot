plugins { alias(libs.plugins.component.framework) }

description =
    "Framework to simplify the creation of a spring boot application. " +
            "The functional test functionalities."

publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootFileTest)
    api(projects.sakuraBootMapperApi)
    api(libs.jackson.databind)
    api(libs.junit.jupiter.api)
    api(libs.rest.assured)
    api(libs.spring.boot.test)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    api(libs.spring.test)
    api(libs.spring.web)
    implementation(projects.sakuraBootBasicApi)
    implementation(projects.sakuraBootSpecificationApi)
    implementation(libs.assertj.core)
    implementation(libs.cache.api)
    implementation(libs.commons.lang3)
    implementation(libs.evo.inflector)
    implementation(libs.hamcrest)
    implementation(libs.jackson.annotations)
    implementation(libs.rest.assured.json.path)
    implementation(libs.spring.beans)
    implementation(libs.spring.context.support)
    compileOnly(libs.jackson.core)
    compileOnly(libs.mapstruct)
    compileOnly(libs.spring.data.jpa)
}

configurations { api { exclude("commons-logging", "commons-logging") } }
