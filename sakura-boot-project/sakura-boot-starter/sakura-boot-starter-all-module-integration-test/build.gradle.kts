plugins { alias(libs.plugins.component.starter) }

description = "Starter for the integration test of all the modules."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasicIntegrationTest)
    api(projects.sakuraBootHypermedia)
    api(projects.sakuraBootMapper)
    api(projects.sakuraBootSpecification)
    api(libs.hibernate.jcache)
}
