package org.sansenshimizu.gradle.report

plugins {
    java
    `jacoco-report-aggregation`
    id("org.sansenshimizu.gradle.base.dependency-rules")
    id("org.sansenshimizu.gradle.base.lifecycle")
}

configurations.aggregateCodeCoverageReportResults {
    extendsFrom(configurations["internal"])
}

val exclude =
    listOf("**/**_.class", "**/**MapperImpl.class", "**/**Application.class")

fun excludeFiles(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map { fileTree(it) { setExcludes(exclude) } }
        )
    )
}

reporting {
    reports {
        val unitTestCodeCoverageReport by
            creating(JacocoCoverageReport::class) {
                testType = TestSuiteType.UNIT_TEST

                reportTask {
                    group = "reporting"
                    excludeFiles(classDirectories)
                }
            }
        val integrationTestCodeCoverageReport by
            creating(JacocoCoverageReport::class) {
                testType = TestSuiteType.INTEGRATION_TEST

                reportTask {
                    group = "reporting"
                    excludeFiles(classDirectories)
                }
            }
        val functionalTestCodeCoverageReport by
            creating(JacocoCoverageReport::class) {
                testType = TestSuiteType.FUNCTIONAL_TEST

                reportTask {
                    group = "reporting"
                    excludeFiles(classDirectories)
                }
            }
        val allTestCodeCoverageReport by
            creating(JacocoCoverageReport::class) {
                testType = "all-tests"

                reportTask {
                    group = "reporting"

                    dependsOn(
                        unitTestCodeCoverageReport.reportTask.get(),
                        integrationTestCodeCoverageReport.reportTask.get(),
                        functionalTestCodeCoverageReport.reportTask.get()
                    )

                    classDirectories.setFrom(
                        unitTestCodeCoverageReport.reportTask
                            .get()
                            .classDirectories,
                        integrationTestCodeCoverageReport.reportTask
                            .get()
                            .classDirectories,
                        functionalTestCodeCoverageReport.reportTask
                            .get()
                            .classDirectories
                    )

                    executionData.setFrom(
                        unitTestCodeCoverageReport.reportTask
                            .get()
                            .executionData,
                        integrationTestCodeCoverageReport.reportTask
                            .get()
                            .executionData,
                        functionalTestCodeCoverageReport.reportTask
                            .get()
                            .executionData
                    )

                    excludeFiles(classDirectories)
                }
            }
    }
}
