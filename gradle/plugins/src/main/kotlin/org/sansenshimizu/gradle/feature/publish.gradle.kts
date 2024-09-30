package org.sansenshimizu.gradle.feature

plugins {
    java
    `maven-publish`
    signing
    id("org.sansenshimizu.gradle.base.identity")
}

publishing {
    publications.register<MavenPublication>("mavenJava") {
        from(components["java"])
        versionMapping { allVariants { fromResolutionResult() } }
        pom {
            name = project.name
            inceptionYear = "2024"
            url = "https://github.com/Sansen-Shimizu/sakura-boot"

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }
            developers {
                developer {
                    id = "malcolm"
                    name = "Malcolm Rozé"
                    email = "malcolm@sansenshimizu.com"
                    url = "https://github.com/malcolmSansen"
                    organization = "株式会社さんせん清水"
                    organizationUrl = "https://sansen-shimizu.co.jp/"
                }
            }
            scm {
                url = "https://github.com/Sansen-Shimizu/sakura-boot"
                connection = "scm:git:git://github.com/Sansen-Shimizu/sakura-boot.git"
                developerConnection = "scm:git:ssh://git@github.com/Sansen-Shimizu/sakura-boot.git"
            }
        }
    }
}

signing.sign(publishing.publications["mavenJava"])
