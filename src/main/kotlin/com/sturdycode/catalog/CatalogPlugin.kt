package com.sturdycode.catalog

import com.android.build.gradle.*
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.api.TestedVariant
import org.gradle.api.*
import org.gradle.api.tasks.TaskProvider

class CatalogPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        listOf(
                AppPlugin::class.java,
                TestPlugin::class.java,
                LibraryPlugin::class.java,
                FeaturePlugin::class.java
        ).forEach {
            project.plugins.withType(it).configureEach { plugin ->
                applyToExtension(project, plugin.extension)
            }
        }
    }

    private fun applyToExtension(project: Project, extension: BaseExtension) {
        when(extension) {
            is AppExtension -> { applyToBaseVariants(project, extension,  extension.applicationVariants) }
            is TestExtension -> { applyToBaseVariants(project, extension, extension.applicationVariants) }
            is LibraryExtension -> { applyToBaseVariants(project, extension, extension.libraryVariants) }
            is FeatureExtension -> { applyToBaseVariants(project, extension, extension.featureVariants) }
            else -> throw GradleException("Unhandled extension type: $extension")
        }
    }

    private fun applyToBaseVariants(project: Project, extension: BaseExtension, variants: DomainObjectSet<out BaseVariant>) {
        adbBridge.initializeAdbExe(extension.adbExecutable)
        variants.configureEach { variant ->
            if (variant !is TestedVariant || variant.testVariant == null) {
                return@configureEach
            }

            val recorderGroup = adbBridge.createRecorderGroup()

            val slug = variant.name.capitalize()
            val printerTask = project.tasks.register("printConnected${slug}AndroidTest", LogCatPrinterTask::class.java)
            printerTask.configure { task ->
                task.setLogCatRecorderGroup(recorderGroup)
                task.group = "Verification"
                task.description = "Print logcat for ${variant.name} variant."
                task.outputDir = project.file("${project.buildDir}/outputs/androidTest-results/logcat")
                task.outputs.upToDateWhen { false }
            }

            val testProvider : TaskProvider<Task> = variant.testVariant.connectedInstrumentTestProvider
            testProvider.configure { task ->
                task.doFirst {
                    recorderGroup.attach()
                }
                task.finalizedBy(printerTask)
            }
        }
    }

    companion object {
        private val adbBridge = AdbBridgeManager()
    }
}
