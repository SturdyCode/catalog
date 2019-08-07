package com.sturdycode.catalog

import com.android.ddmlib.Log
import com.android.ddmlib.logcat.LogCatHeader
import com.android.ddmlib.logcat.LogCatMessage
import com.android.utils.SparseArray
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LogCatPrinterTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var txtFile: File
    private lateinit var htmlFile: File
    private lateinit var printer: LogCatPrinter

    @Before
    fun setUp() {
        txtFile = tmpFolder.newFile()
        htmlFile = tmpFolder.newFile()
        printer = LogCatPrinter(txtFile, htmlFile)
    }

    @Test
    fun print_emptyList_shouldNotPrintAnything() {
        val records = Records(arrayListOf(), SparseArray())

        printer.print(records)

        assertEquals("", txtFile.readText())
        assertEquals("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <link href="logcat.css" media="all" rel="stylesheet"/>
            </head>
            <body>
            <div class="links-container">
            </div>
            <ul>
            </ul>
            </body>
            </html>
        """.trimIndent(), htmlFile.readText().trim())
    }

    @Test
    fun print_starter_shouldPrintHeader() {
        val startMessage = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        val messages = arrayListOf(startMessage)
        val starters = SparseArray<Starter>()
        starters.put(0, Starter("com.example.romainpiel.myapplication.MainActivityTest", "dumbTest"))
        val records = Records(messages, starters)

        printer.print(records)

        assertTrue(txtFile.readText().contains("-- started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)\n"))
        assertEquals("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <link href="logcat.css" media="all" rel="stylesheet"/>
            </head>
            <body>
            <div class="links-container">
            <a class="link" href="#com.example.romainpiel.myapplication.MainActivityTest.dumbTest">MainActivityTest > dumbTest</a>
            </div>
            <ul>
            <li class="start-container">
            <a href="#com.example.romainpiel.myapplication.MainActivityTest.dumbTest" id="com.example.romainpiel.myapplication.MainActivityTest.dumbTest" class="start">MainActivityTest > dumbTest</a>
            </li>
            </ul>
            </body>
            </html>
        """.trimIndent(), htmlFile.readText().trim())
    }

    @Test
    fun print_nonStarter_shouldPrintLine() {
        val message = newMessage("Cupcake", "I'm the sweetest!")
        val messages = arrayListOf(message)
        val starters = SparseArray<Starter>()
        val records = Records(messages, starters)

        printer.print(records)

        assertTrue(txtFile.readText().contains("-- I'm the sweetest!\n"))
        assertEquals("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <link href="logcat.css" media="all" rel="stylesheet"/>
            </head>
            <body>
            <div class="links-container">
            </div>
            <ul>
            <li>
            <div class="tag" style="color:rgb(160, 162, 170);">Cupcake</div>
            <div class="level info">I</div>
            <div class="message">I'm the sweetest!</div>
            </li>
            </ul>
            </body>
            </html>
        """.trimIndent(), htmlFile.readText().trim())
    }

    private fun newMessage(tag: String, message: String) : LogCatMessage {
        val header = LogCatHeader(Log.LogLevel.INFO, 123, 0, "MyApp", tag, Instant.now())
        return LogCatMessage(header, message)
    }
}
