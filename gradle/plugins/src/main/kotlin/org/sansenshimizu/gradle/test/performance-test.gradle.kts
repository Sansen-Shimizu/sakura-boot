package org.sansenshimizu.gradle.test

plugins {
    java
    `jvm-test-suite`
}

testing.suites.register<JvmTestSuite>("performanceTest") {
    testType = TestSuiteType.PERFORMANCE_TEST
    targets.named("performanceTest") { testTask { group = "verification" } }
    dependencies { implementation(project()) }
}
