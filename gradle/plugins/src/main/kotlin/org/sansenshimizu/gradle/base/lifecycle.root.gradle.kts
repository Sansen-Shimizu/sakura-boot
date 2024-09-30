package org.sansenshimizu.gradle.base

plugins {
    base
    id("org.sansenshimizu.gradle.base.lifecycle")
}

defaultTasks("tasks")

tasks.named<TaskReportTask>("tasks") { displayGroup = "build" }
