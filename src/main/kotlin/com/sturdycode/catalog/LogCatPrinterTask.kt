package com.sturdycode.catalog

import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class LogCatPrinterTask : DefaultTask() {

    private lateinit var recorderGroup : LogCatRecorderGroup

    @get:OutputDirectory
    internal lateinit var outputDir: File

    fun setLogCatRecorderGroup(recorderGroup: LogCatRecorderGroup) {
        this.recorderGroup = recorderGroup
    }

    @TaskAction
    fun run() {
        outputDir.mkdirs()

        recorderGroup.close()
        recorderGroup.getRecordedDevices().forEach { recordedDevice ->
            this::class.java.classLoader.getResourceAsStream("logcat.css").use { input ->
                File(outputDir.absolutePath, "logcat.css").outputStream().use { output ->
                    IOUtils.copy(input, output)
                }
            }
            val device = recordedDevice.device
            val txtFileName = "logcat-${device.name.replace(' ', '_')}.txt"
            val txtFile = File(outputDir, txtFileName)
            val htmlFileName = "logcat-${device.name.replace(' ', '_')}.html"
            val htmlFile = File(outputDir, htmlFileName)
            val printer = LogCatPrinter(txtFile, htmlFile)
            printer.print(recordedDevice.recorder.getRecords())
        }
    }
}
