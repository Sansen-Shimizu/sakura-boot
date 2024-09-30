package org.sansenshimizu.gradle.feature

plugins { `java-platform` }

dependencies.constraints {
    val libs = versionCatalogs.named("libs")
    val catalogEntries =
        libs.libraryAliases.map { libs.findLibrary(it).get().get() }
    catalogEntries
        .filter { it.version != null }
        .forEach {
            api(it) {
                version { it.version?.let { version -> strictly(version) } }
            }
        }
}
