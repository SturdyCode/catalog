package com.sturdycode.catalog

import com.android.ddmlib.logcat.LogCatReceiverTask

data class Device(
        val name: String,
        val task: LogCatReceiverTask) {

    override fun toString(): String {
        return name
    }
}
