plugins { alias(libs.plugins.component.starter) }

description = "Predefined starter for the functional test of the basic module. " +
        "Include the basic module, spring boot starter, and postgresql " +
        "testcontainers."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasicFunctionalTest)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.test)
    api(libs.jsr305)
    runtimeOnly(libs.spring.boot.testcontainers)
    runtimeOnly(libs.testcontainers.postgresql)
}
