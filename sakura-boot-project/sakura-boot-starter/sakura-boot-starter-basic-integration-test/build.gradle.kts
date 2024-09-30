plugins { alias(libs.plugins.component.starter) }

description = "Starter for the integration test of the basic module."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasic)
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootIntegrationTest)
    api(libs.jackson.databind)
    api(libs.spring.beans)
    api(libs.spring.boot.test)
    api(libs.spring.boot.test.autoconfigure)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.test)
    runtimeOnly(libs.hamcrest)
    runtimeOnly(libs.jsonassert)
}
