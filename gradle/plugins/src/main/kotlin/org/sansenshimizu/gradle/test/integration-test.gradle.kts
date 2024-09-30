package org.sansenshimizu.gradle.test

plugins {
    java
    `jvm-test-suite`
    jacoco
}

testing.suites.register<JvmTestSuite>("integrationTest") {
    testType = TestSuiteType.INTEGRATION_TEST
    targets.named("integrationTest") {
        testTask {
            group = "verification"
            notCompatibleWithConfigurationCache(
                "integration test should not run in parallel across projects."
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
