plugins { alias(libs.plugins.component.starter) }

description = "Starter for all the modules."
publishing.publications.getByName<MavenPublication>("mavenJava") {
    pom.description = description
}

dependencies {
    api(projects.sakuraBootStarterBasic)
    api(projects.sakuraBootBulk)
    api(projects.sakuraBootBulkApi)
    api(projects.sakuraBootCache)
    api(projects.sakuraBootCacheApi)
    api(projects.sakuraBootHypermedia)
    api(projects.sakuraBootHypermediaApi)
    api(projects.sakuraBootLogApi)
    api(projects.sakuraBootMapper)
    api(projects.sakuraBootMapperApi)
    api(projects.sakuraBootSpecification)
    api(projects.sakuraBootSpecificationApi)
    api(libs.spring.hateoas)
    runtimeOnly(projects.sakuraBootLog)
    runtimeOnly(projects.sakuraBootOpenapi)
    runtimeOnly(libs.springdoc.openapi.starter.webmvc.ui)
}
