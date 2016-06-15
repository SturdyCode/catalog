package com.songkick.catalog
import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class LogCatPrinterTask extends DefaultTask {

    @Input
    List<Device> devices

    @OutputDirectory
    File outputDir

    @TaskAction
    void run() {
        outputDir.mkdirs()
        devices.each { device ->
            device.task.stop()
            device.task.removeLogCatListener(device.recorder)

            def txtFileName = "logcat-${device.name.replace(' ', '_')}.txt"
            def txtFile = new File(outputDir.absolutePath, txtFileName)
            def htmlFileName = "logcat-${device.name.replace(' ', '_')}.html"
            def htmlFile = new File(outputDir.absolutePath, htmlFileName)
            LogCatPrinter printer = new LogCatPrinter(txtFile, htmlFile)
            printer.print(device.recorder.recordedMessages)
        }
        devices.clear()

        AndroidDebugBridge.terminate();
    }
}