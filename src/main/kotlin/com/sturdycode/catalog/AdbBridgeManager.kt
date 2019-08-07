package com.sturdycode.catalog

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.logcat.LogCatReceiverTask
import org.apache.log4j.Logger
import org.gradle.api.GradleException
import java.io.Closeable
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier

internal class AdbBridgeManager : Closeable, AndroidDebugBridge.IDeviceChangeListener {
    private val devices : MutableList<Device> = arrayListOf()
    private var adbExe: File? = null
    private val bridgeOpen = AtomicBoolean()

    override fun close() {
        if (bridgeOpen.compareAndSet(true, false)) {
            AndroidDebugBridge.removeDeviceChangeListener(this)
            AndroidDebugBridge.terminate()
        }
    }

    override fun deviceConnected(device: IDevice) {
        LOGGER.info("Device connected: $device")
    }

    override fun deviceDisconnected(device: IDevice) {
        LOGGER.info("Device disconnected: $device")
    }

    @Override
    override fun deviceChanged(device: IDevice, changeMask: Int) {
        LOGGER.debug("Device changed: $device (changeMask: $changeMask)")
    }

    fun initializeAdbExe(adbExeFile: File) {
        if (adbExe == null) {
            LOGGER.debug("Initialized ADB executable to: $adbExeFile")
            adbExe = adbExeFile
        }
    }

    fun createRecorderGroup() : LogCatRecorderGroup {
        return LogCatRecorderGroup(Supplier {
            if (bridgeOpen.compareAndSet(false, true)) {
                establishBridge()
                Runtime.getRuntime().addShutdownHook(Thread {
                    close()
                })
            }
            devices
        })
    }

    private fun establishBridge() {
        LOGGER.info("Establishing ADB bridge")
        val adbExe = this.adbExe
                ?: throw GradleException("ADB executable location has not been initialized")
        val adb = AndroidDebugBridge.createBridge(adbExe.absolutePath, false)
                ?: throw GradleException("Failed to obtain ADB bridge")

        AndroidDebugBridge.initIfNeeded(false)
        AndroidDebugBridge.addDeviceChangeListener(this)
        if (adb.hasInitialDeviceList()) {
            runLogCat(adb)
        } else {
            var attempts = 0
            while (!adb.hasInitialDeviceList() && attempts < MAX_INIT_ATTEMPTS) {
                Thread.sleep(WAIT_BEFORE_NEXT_INIT_ATTEMPT_MS)
                runLogCat(adb)
                attempts++
            }
            if (!adb.hasInitialDeviceList()) {
                throw GradleException("Unable to establish ADB bridge")
            }
        }
    }

    private fun runLogCat(adb: AndroidDebugBridge) {
        adb.devices?.forEach { device ->
            val receiverTask = LogCatReceiverTask(device)
            devices.add(Device(device.name, receiverTask))
            Thread { receiverTask.run() }.start()
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(this::class.java.simpleName)
        private const val MAX_INIT_ATTEMPTS = 100
        private const val WAIT_BEFORE_NEXT_INIT_ATTEMPT_MS = 50L
    }
}
