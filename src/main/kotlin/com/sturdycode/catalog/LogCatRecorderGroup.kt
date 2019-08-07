package com.sturdycode.catalog

import java.io.Closeable
import java.util.function.Supplier

class LogCatRecorderGroup(private val onAttach: Supplier<List<Device>>) : Closeable {

    private val recordedDevices = arrayListOf<RecordedDevice>()

    fun attach() {
        onAttach.get().forEach {
            val recordedDevice = RecordedDevice(it)
            recordedDevices.add(recordedDevice)
            recordedDevice.attach()
        }
    }

    @Override
    override fun close() {
        recordedDevices.forEach{
            it.close()
        }
    }

    fun getRecordedDevices() : List<RecordedDevice> {
        return recordedDevices
    }
}
