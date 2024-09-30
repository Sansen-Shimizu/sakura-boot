plugins { alias(libs.plugins.component.starter) }

description = "Starter for the functional test of all the modules."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasicFunctionalTest)
    api(projects.sakuraBootMapperTest)
    api(projects.sakuraBootSpecification)
    api(libs.hibernate.jcache)
}
