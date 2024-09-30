dependencyResolutionManagement {
    versionCatalogs.register("libs") {
        from(files("../libs.versions.toml"))
    }
    repositories.gradlePluginPortal()
}

rootProject.name = "sakura-boot-plugin"
