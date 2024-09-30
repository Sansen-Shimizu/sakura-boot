plugins {
    alias(libs.plugins.base.lifecycle.root)
    alias(libs.plugins.check.dependencies.root)
    alias(libs.plugins.check.format.gradle.root)
    id("com.gradleup.nmcp").version("0.0.9")
}

nmcp {
    publishAllProjectsProbablyBreakingProjectIsolation {
        username = (project.findProperty("mavenCentralUsername") ?: System.getenv("mavenCentralUsername")) as String
        password = (project.findProperty("mavenCentralPassword") ?: System.getenv("mavenCentralPassword")) as String
        publicationType = "USER_MANAGED"
    }
}
