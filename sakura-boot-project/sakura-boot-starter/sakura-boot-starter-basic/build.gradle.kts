plugins { alias(libs.plugins.component.starter) }

description = "Starter for the basic module."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasic)
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCore)
    api(libs.commons.lang3)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.jakarta.persistence.api)
    api(libs.spring.boot)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.data.jpa)
    api(libs.spring.web)
}
