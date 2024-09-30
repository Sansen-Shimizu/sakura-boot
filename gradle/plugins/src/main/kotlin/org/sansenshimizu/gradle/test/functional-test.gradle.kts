package org.sansenshimizu.gradle.test

plugins {
    java
    `jvm-test-suite`
    jacoco
}

testing.suites.register<JvmTestSuite>("functionalTest") {
    testType = TestSuiteType.FUNCTIONAL_TEST
    targets.named("functionalTest") {
        testTask {
            group = "verification"
            notCompatibleWithConfigurationCache(
                "functional test should not run in parallel across projects."
            )
            jvmArgs =
                listOf(
                    "-javaagent:${
                        configurations.getByName("byteBuddyAgent")
                            .singleFile.absolutePath
                    }",
                    "-Xshare:off"
                )
        }
    }

    dependencies { implementation(project()) }
}
