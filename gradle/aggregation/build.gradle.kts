import org.sansenshimizu.gradle.util.ProjectUtils.aggregateDir

plugins {
    java
    alias(libs.plugins.base.lifecycle)
    alias(libs.plugins.base.dependency.rules)
    alias(libs.plugins.check.format.gradle)
    alias(libs.plugins.report.code.coverage)
    alias(libs.plugins.report.plugin.analysis)
    alias(libs.plugins.report.sbom)
    alias(libs.plugins.report.test)
}

aggregateDir("sakura-boot-project", 2)

aggregateDir("example-project", 2)
