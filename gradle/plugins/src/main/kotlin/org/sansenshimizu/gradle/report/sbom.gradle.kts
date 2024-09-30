package org.sansenshimizu.gradle.report

plugins {
    java
    id("org.cyclonedx.bom")
}

tasks.cyclonedxBom {
    notCompatibleWithConfigurationCache(
        "https://github.com/CycloneDX/cyclonedx-gradle-plugin/issues/193"
    )
    includeConfigs.add(configurations.runtimeClasspath.name)
    destination = layout.buildDirectory.dir("reports/sbom").get().asFile
}
