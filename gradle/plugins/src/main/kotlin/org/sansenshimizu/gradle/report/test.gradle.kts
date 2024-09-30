package org.sansenshimizu.gradle.report

plugins {
    java
    `test-report-aggregation`
    id("org.sansenshimizu.gradle.base.dependency-rules")
    id("org.sansenshimizu.gradle.base.lifecycle")
}

configurations.aggregateTestReportResults {
    extendsFrom(configurations["internal"])
}

reporting {
    reports {
        val unitTestAggregateTestReport by
            creating(AggregateTestReport::class) {
                testType = TestSuiteType.UNIT_TEST

                reportTask { group = "reporting" }
            }
        val integrationTestAggregateTestReport by
            creating(AggregateTestReport::class) {
                testType = TestSuiteType.INTEGRATION_TEST

                reportTask { group = "reporting" }
            }
        val functionalTestAggregateTestReport by
            creating(AggregateTestReport::class) {
                testType = TestSuiteType.FUNCTIONAL_TEST

                reportTask { group = "reporting" }
            }
        val allTestAggregateTestReport by
            creating(AggregateTestReport::class) {
                testType = "all-tests"

                reportTask {
                    group = "reporting"

                    dependsOn(
                        unitTestAggregateTestReport.reportTask.get(),
                        integrationTestAggregateTestReport.reportTask.get(),
                        functionalTestAggregateTestReport.reportTask.get()
                    )

                    testResults.setFrom(
                        unitTestAggregateTestReport.reportTask
                            .get()
                            .testResults,
                        integrationTestAggregateTestReport.reportTask
                            .get()
                            .testResults,
                        functionalTestAggregateTestReport.reportTask
                            .get()
                            .testResults
                    )
                }
            }
    }
}

tasks.testAggregateTestReport {
    group = "reporting"
    destinationDirectory = layout.buildDirectory.dir("reports/tests")
    testResults.from(
        configurations.aggregateTestReportResults
            .get()
            .incoming
            .artifactView {
                withVariantReselection()
                attributes {
                    attribute(
                        Category.CATEGORY_ATTRIBUTE,
                        objects.named(Category.VERIFICATION)
                    )
                    attribute(
                        TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE,
                        objects.named(TestSuiteType.INTEGRATION_TEST)
                    )
                    attribute(
                        TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE,
                        objects.named(TestSuiteType.FUNCTIONAL_TEST)
                    )
                    attribute(
                        TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE,
                        objects.named(TestSuiteType.PERFORMANCE_TEST)
                    )
                    attribute(
                        VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
                        objects.named(VerificationType.TEST_RESULTS)
                    )
                }
            }
            .files
    )
}

tasks.check { dependsOn(tasks.testAggregateTestReport) }

tasks.buildDependents { setGroup(null) }

tasks.buildNeeded { setGroup(null) }

tasks.jar { setGroup(null) }

sourceSets.all { tasks.named(classesTaskName) { group = null } }
