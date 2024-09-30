package org.sansenshimizu.gradle.util

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider

object DependencyUtils {

    fun getLibrary(
        versionCatalog: VersionCatalog,
        name: String
    ): Provider<MinimalExternalModuleDependency> =
        versionCatalog.findLibrary(name).get()
}
