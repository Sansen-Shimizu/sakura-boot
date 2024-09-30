plugins {
    alias(libs.plugins.component.framework)
    alias(libs.plugins.dependency.lombok)
    alias(libs.plugins.dependency.lombok.test)
}

description = "Framework to simplify the creation of a spring boot application. " +
        "The basic functionalities."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootBasicApi)
    api(projects.sakuraBootCore)
    api(projects.sakuraBootLogApi)
    api(libs.aspectjweaver)
    api(libs.jakarta.persistence.api)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.data.commons)
    api(libs.spring.tx)
    implementation(projects.sakuraBootLog)
    implementation(libs.commons.lang3)
    implementation(libs.hibernate.core)
    implementation(libs.slf4j.api)
    compileOnly(libs.hibernate.jpamodelgen)
    compileOnly(libs.spring.data.jpa)
    annotationProcessor(libs.hibernate.jpamodelgen)

    testImplementation(projects.sakuraBootCoreTest)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito.core)
    testCompileOnly(libs.hibernate.jpamodelgen)
    testRuntimeOnly(libs.logback.classic)
    testRuntimeOnly(libs.spring.data.jpa)
    testAnnotationProcessor(libs.hibernate.jpamodelgen)
}

dependencyAnalysis {
    issues {
        onUnusedAnnotationProcessors {
            exclude(libs.hibernate.jpamodelgen)
            exclude(libs.lombok)
        }
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.apply {
            compilerArgs.add("-AaddSuppressWarningsAnnotation=true")
        }
    }
}
