plugins { alias(libs.plugins.component.starter) }

description = "Starter for the unit test of the basic module."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicTest)
    api(projects.sakuraBootCoreTest)
    api(libs.mockito.core)
}
