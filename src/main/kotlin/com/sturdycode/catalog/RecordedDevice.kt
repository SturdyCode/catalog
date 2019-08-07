package com.sturdycode.catalog

import org.apache.log4j.Logger
import java.io.Closeable

class RecordedDevice(internal val device: Device): Closeable {

    internal val recorder = LogCatRecorder()

    fun attach() {
        LOGGER.info("Attaching log listener to device: $device")
        device.task.addLogCatListener(recorder)
    }

    override fun close() {
        LOGGER.info("Detaching log listener from device: $device")
        device.task.removeLogCatListener(recorder)
    }

    companion object {
        private val LOGGER = Logger.getLogger(this::class.java.simpleName)
    }

}