plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for the integration test of the basic module. " +
        "Include the basic module, spring boot starter, and h2 database."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasicIntegrationTest)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.test)
    api(libs.jsr305)
    runtimeOnly(libs.h2)
}
