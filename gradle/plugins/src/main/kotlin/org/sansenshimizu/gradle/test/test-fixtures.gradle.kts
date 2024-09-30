package org.sansenshimizu.gradle.test

plugins { `java-test-fixtures` }

tasks.testFixturesJar { setGroup(null) }
