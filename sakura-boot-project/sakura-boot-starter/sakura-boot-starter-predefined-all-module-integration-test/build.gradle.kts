plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for the integration test of all the modules. " +
        "Include all the modules, spring boot starter, and h2 database."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterPredefinedBasicIntegrationTest)
    api(projects.sakuraBootStarterAllModuleIntegrationTest)
}
