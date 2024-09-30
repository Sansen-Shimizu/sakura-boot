plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for the functional test of all the modules. " +
        "Include all the modules, spring boot starter, postgresql " +
        "testcontainers, and mapstruct."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterPredefinedBasicFunctionalTest)
    api(projects.sakuraBootStarterAllModuleFunctionalTest)
    api(libs.mapstruct)
}
