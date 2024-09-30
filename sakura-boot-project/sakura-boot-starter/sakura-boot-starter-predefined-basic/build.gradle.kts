plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for the basic module. " +
        "Include the basic module, spring boot starter, and postgresql database."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasic)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.jsr305)
    runtimeOnly(libs.postgresql)
}
