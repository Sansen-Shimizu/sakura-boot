plugins { `kotlin-dsl` }

dependencies {
    implementation(libs.antora)
    implementation(libs.classpath.collision.detector)
    implementation(libs.cyclonedx.gradle.plugin)
    implementation(libs.dependency.analysis.gradle.plugin)
    implementation(libs.jvm.dependency.conflict.resolution)
    implementation(libs.node)
    implementation(libs.spotless.plugin.gradle)
    implementation(libs.spring.boot.gradle.plugin)
}
