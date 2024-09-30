plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for all the modules. " +
        "Include all the modules, spring boot starter, postgresql database, " +
        "mapstruct, and ehcache3."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterPredefinedBasic)
    api(projects.sakuraBootStarterAllModule)
    api(libs.mapstruct)
    runtimeOnly(libs.ehcache) { artifact { classifier = "jakarta" } }
}
