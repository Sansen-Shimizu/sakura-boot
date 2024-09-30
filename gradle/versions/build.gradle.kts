plugins {
    alias(libs.plugins.base.dependency.rules)
    alias(libs.plugins.base.lifecycle)
    alias(libs.plugins.feature.use.all.catalog.versions)
    alias(libs.plugins.check.format.gradle)
}

javaPlatform.allowDependencies()

dependencies { api(platform(libs.spring.boot.dependencies)) }
