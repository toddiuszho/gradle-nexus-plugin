/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.nexus.singleproject

import com.bmuschko.gradle.nexus.AbstractIntegrationTest
import com.bmuschko.gradle.nexus.ExtraArchivePlugin
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.Task
import spock.lang.Issue

/**
 * Nexus plugin archive task integration tests.
 *
 * @author Benjamin Muschko
 */
class SingleProjectArchiveTaskIntegrationTest extends AbstractIntegrationTest {
    def "Adds sources and Javadoc JAR tasks by default for Java project"() {
        given:
        buildFile << """
apply plugin: 'java'
apply plugin: com.bmuschko.gradle.nexus.NexusPlugin
"""

        when:
        GradleProject project = runTasks(integTestDir, 'tasks')

        then:
        Task sourcesJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.SOURCES_JAR_TASK_NAME }
        sourcesJarTask
        sourcesJarTask.description == 'Assembles a jar archive containing the main sources of this project.'
        Task javadocJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME }
        javadocJarTask
        javadocJarTask.description == 'Assembles a jar archive containing the generated Javadoc API documentation of this project.'
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.TESTS_JAR_TASK_NAME }
    }

    def "Adds sources and Javadoc JAR tasks by default for Groovy project"() {
        given:
        buildFile << """
apply plugin: 'java'
apply plugin: com.bmuschko.gradle.nexus.NexusPlugin
"""

        when:
        GradleProject project = runTasks(integTestDir, 'tasks')

        then:
        Task sourcesJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.SOURCES_JAR_TASK_NAME }
        sourcesJarTask
        sourcesJarTask.description == 'Assembles a jar archive containing the main sources of this project.'
        Task javadocJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME }
        javadocJarTask
        javadocJarTask.description == 'Assembles a jar archive containing the generated Javadoc API documentation of this project.'
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.TESTS_JAR_TASK_NAME }
    }

    def "Adds tests JAR task if configured"() {
        when:
        buildFile << """
apply plugin: 'java'
apply plugin: com.bmuschko.gradle.nexus.NexusPlugin

extraArchive {
    tests = true
}
"""
        GradleProject project = runTasks(integTestDir, 'tasks')

        then:
        Task sourcesJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.SOURCES_JAR_TASK_NAME }
        sourcesJarTask
        sourcesJarTask.description == 'Assembles a jar archive containing the main sources of this project.'
        Task javadocJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME }
        javadocJarTask
        javadocJarTask.description == 'Assembles a jar archive containing the generated Javadoc API documentation of this project.'
        Task testsJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.TESTS_JAR_TASK_NAME}
        testsJarTask
        testsJarTask.description == 'Assembles a jar archive containing the test sources of this project.'
    }

    def "Disables additional JAR creation"() {
        when:
        buildFile << """
apply plugin: 'java'
apply plugin: com.bmuschko.gradle.nexus.NexusPlugin

extraArchive {
    sources = false
    javadoc = false
}
"""
        GradleProject project = runTasks(integTestDir, 'tasks')

        then:
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.SOURCES_JAR_TASK_NAME}
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME}
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.TESTS_JAR_TASK_NAME}
    }

    @Issue("https://github.com/bmuschko/gradle-nexus-plugin/issues/8")
    def "Java plugin can be applied after Nexus plugin"() {
        given:
        buildFile << """
apply plugin: com.bmuschko.gradle.nexus.NexusPlugin
apply plugin: 'java'
"""

        when:
        GradleProject project = runTasks(integTestDir, 'tasks')

        then:
        Task sourcesJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.SOURCES_JAR_TASK_NAME }
        sourcesJarTask
        Task javadocJarTask = project.tasks.find { task -> task.name == ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME }
        javadocJarTask
        !project.tasks.find { task -> task.name == ExtraArchivePlugin.TESTS_JAR_TASK_NAME }
    }
}
