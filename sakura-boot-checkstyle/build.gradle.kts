plugins { alias(libs.plugins.feature.publish) }

version = "1.0.1"
description = "Checkstyle configuration used in the sakura-boot project."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}
