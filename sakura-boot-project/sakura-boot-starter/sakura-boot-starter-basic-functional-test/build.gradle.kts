plugins { alias(libs.plugins.component.starter) }

description = "Starter for the functional test of the basic module."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasic)
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCoreTest)
    api(projects.sakuraBootFunctionalTest)
    api(libs.jackson.databind)
    api(libs.spring.beans)
    api(libs.spring.boot.test)
    api(libs.spring.context)
    api(libs.spring.core)
}
