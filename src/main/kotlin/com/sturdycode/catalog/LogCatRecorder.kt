package com.sturdycode.catalog

import com.android.ddmlib.logcat.LogCatListener
import com.android.ddmlib.logcat.LogCatMessage
import com.android.utils.SparseArray
import java.util.regex.Pattern

class LogCatRecorder : LogCatListener {
    private var lastRecordedPid = -1
    private var pid = -1
    private val recordedMessages: MutableList<LogCatMessage> = arrayListOf()
    private val recordedStarters = SparseArray<Starter>()

    @Override
    override fun log(logCatMessages: List<LogCatMessage>) {
        logCatMessages.forEach { logCatMessage ->
            if (pid == -1) {
                val match = MESSAGE_START.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER == logCatMessage.tag) {
                    pid = logCatMessage.pid
                    if (lastRecordedPid != pid) {
                        clearRecordedMessages()
                    }

                    val position = recordedMessages.size
                    record(logCatMessage)
                    val testName = match.group(1)
                    val className = match.group(2)
                    recordedStarters.put(position, Starter(className, testName))
                }
            } else {
                if (pid == logCatMessage.pid) {
                    record(logCatMessage)
                }

                val match = MESSAGE_END.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER == logCatMessage.tag) {
                    pid = -1
                }
            }
        }
    }

    internal fun getRecords(): Records {
        return Records(recordedMessages, recordedStarters)
    }

    private fun record(message: LogCatMessage) {
        recordedMessages.add(message)
        lastRecordedPid = message.pid
    }

    private fun clearRecordedMessages() {
        recordedMessages.clear()
        recordedStarters.clear()
    }

    companion object {
        private const val TEST_RUNNER = "TestRunner"
        private val MESSAGE_START = Pattern.compile("started: ([^(]+)\\(([^)]+)\\)")
        private val MESSAGE_END = Pattern.compile("finished: [^(]+\\([^)]+\\)")
    }
}