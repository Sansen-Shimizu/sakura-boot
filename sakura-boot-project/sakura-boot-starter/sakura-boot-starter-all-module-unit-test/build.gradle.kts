plugins { alias(libs.plugins.component.starter) }

description = "Starter for the unit test of all the modules."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasicUnitTest)
    api(projects.sakuraBootBulkTest)
    api(projects.sakuraBootCacheTest)
    api(projects.sakuraBootFileTest)
    api(projects.sakuraBootHypermediaTest)
    api(projects.sakuraBootMapperTest)
    api(projects.sakuraBootSpecificationTest)
}
