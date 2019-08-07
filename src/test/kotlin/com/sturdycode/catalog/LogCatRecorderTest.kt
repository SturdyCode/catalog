package com.sturdycode.catalog

import com.android.ddmlib.Log
import com.android.ddmlib.logcat.LogCatHeader
import com.android.ddmlib.logcat.LogCatMessage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.time.Instant
import kotlin.test.assertEquals

internal class LogCatRecorderTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var recorder: LogCatRecorder

    @Before
    fun setUp() {
        recorder = LogCatRecorder()
    }

    @Test
    fun log_shouldNotRecordBeforeTestRunner() {
        val message = newMessage("Romain", "Bonjour, comment vas-tu?")
        val messages = arrayListOf(message)

        recorder.log(messages)

        val records = recorder.getRecords()
        val recordedMessages = records.messages
        val starters = records.starters

        assertEquals(0, recordedMessages.size)
        assertEquals(0, starters.size())
    }

    @Test
    fun log_shouldRecordSamePidAfterStart() {
        val start = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        val message = newMessage("Romain", "Bonjour, comment vas-tu?", 1)
        val messages = arrayListOf(start, message)

        recorder.log(messages)

        val recordedMessages = recorder.getRecords().messages
        assertEquals(2, recordedMessages.size)
        assertEquals(start, recordedMessages[0])
        assertEquals(message, recordedMessages[1])
    }

    @Test
    fun log_shouldNotRecordDifferentPidAfterStart() {
        val start = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        val message = newMessage("Romain", "Bonjour, comment vas-tu?", 2)
        val messages = arrayListOf(start, message)

        recorder.log(messages)

        val recordedMessages = recorder.getRecords().messages
        assertEquals(1, recordedMessages.size)
        assertEquals(start, recordedMessages[0])
    }

    @Test
    fun log_shouldRecordOnlyLastTestRunnerPid() {
        val startTestPid1 = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        val endTestPid1 = newMessage("TestRunner", "finished: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        val startTestPid2 = newMessage("TestRunner", "started: dumbTest(com.bakery.CupcakeActivityTest)", 2)
        val messages = arrayListOf(startTestPid1, endTestPid1, startTestPid2)

        recorder.log(messages)

        val recordedMessages = recorder.getRecords().messages
        assertEquals(1, recordedMessages.size)
        assertEquals(startTestPid2, recordedMessages[0])
    }

    @Test
    fun log_shouldRecordOneStarter() {
        val message = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        val messages = arrayListOf(message)

        recorder.log(messages)

        val starters = recorder.getRecords().starters
        assertEquals(1, starters.size())
        val starter = starters[0]
        assertEquals("com.example.romainpiel.myapplication.MainActivityTest", starter.className)
        assertEquals("dumbTest", starter.testName)
    }

    @Test
    fun log_shouldRecordTwoStarters() {
        val startTest1 = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        val endTest1 = newMessage("TestRunner", "finished: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        val startTest2 = newMessage("TestRunner", "started: anotherTest(com.example.romainpiel.myapplication.MainActivityTest)")
        val messages = arrayListOf(startTest1, endTest1, startTest2)

        recorder.log(messages)

        val starters = recorder.getRecords().starters
        assertEquals(2, starters.size())
        val starter1 = starters.get(0)
        assertEquals("com.example.romainpiel.myapplication.MainActivityTest", starter1.className)
        assertEquals("dumbTest", starter1.testName)
        val starter2 = starters.get(2)
        assertEquals("com.example.romainpiel.myapplication.MainActivityTest", starter2.className)
        assertEquals("anotherTest", starter2.testName)
    }

    private fun newMessage(tag: String, message: String, pid: Int = DEFAULT_PID) : LogCatMessage {
        val header = LogCatHeader(Log.LogLevel.INFO, pid, 0, "MyApp", tag, Instant.now())
        return LogCatMessage(header, message)
    }

    companion object {
        private const val DEFAULT_PID = 12345678
    }
}
