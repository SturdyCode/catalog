package com.sturdycode.catalog

import java.awt.Color
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class LogCatPrinter(private val txtFile: File, private val htmlFile: File) {

    private val colorCache = HashMap<String, String>()

    fun print(records: Records) {
        PrintWriter(BufferedWriter(FileWriter(txtFile, false), 16 * 1024)).use { txtPrintWriter ->
            PrintWriter(BufferedWriter(FileWriter(htmlFile, false), 16 * 1024)).use { htmlPrintWriter ->
                htmlPrintWriter.println("<!DOCTYPE html>")
                htmlPrintWriter.println("<html lang=\"en\">")
                htmlPrintWriter.println("<head>")
                htmlPrintWriter.println("<link href=\"logcat.css\" media=\"all\" rel=\"stylesheet\"/>")
                htmlPrintWriter.println("</head>")
                htmlPrintWriter.println("<body>")

                htmlPrintWriter.println("<div class=\"links-container\">")
                for (i in 0 until records.starters.size()) {
                    val starter = records.starters.valueAt(i)
                    val classSimpleName = starter.className.substringAfterLast(".")
                    htmlPrintWriter.println("<a class=\"link\" href=\"#${starter.className}.${starter.testName}\">$classSimpleName > ${starter.testName}</a>")
                }
                htmlPrintWriter.println("</div>")

                htmlPrintWriter.println("<ul>")

                for ((i, logCatMessage) in records.messages.withIndex()) {
                    txtPrintWriter.println("${logCatMessage.pid} ${logCatMessage.timestamp} -- ${logCatMessage.message}")

                    if (records.starters.get(i) != null) {
                        htmlPrintWriter.println("<li class=\"start-container\">")
                        val starter = records.starters.get(i)
                        val classSimpleName = starter.className.substringAfterLast(".")
                        htmlPrintWriter.println("<a href=\"#${starter.className}.${starter.testName}\" id=\"${starter.className}.${starter.testName}\" class=\"start\">$classSimpleName > ${starter.testName}</a>")
                    } else {
                        htmlPrintWriter.println("<li>")
                        val tagColor = stringToRGB(logCatMessage.tag)
                        htmlPrintWriter.println("<div class=\"tag\" style=\"color:$tagColor;\">${logCatMessage.tag}</div>")
                        htmlPrintWriter.println("<div class=\"level ${logCatMessage.logLevel.stringValue.toLowerCase()}\">${logCatMessage.logLevel.stringValue.toUpperCase()[0]}</div>")
                        htmlPrintWriter.println("<div class=\"message\">${logCatMessage.message}</div>")
                    }
                    htmlPrintWriter.println("</li>")
                }
                htmlPrintWriter.println("</ul>")
                htmlPrintWriter.println("</body>")
                htmlPrintWriter.println("</html>")
            }
        }
    }

    private fun stringToRGB(s: String) : String {
        return if (colorCache.containsKey(s)) {
            colorCache[s]!!
        } else {
            val hexColor = String.format("#%06X", (0xFFFFFF and s.hashCode()))
            val readableRGB = readableRGB(hexColor, 0.5f)
            colorCache[s] = readableRGB
            readableRGB
        }
    }

    @Suppress("SameParameterValue")
    private fun readableRGB(hexColor: String, amount: Float) : String {
        val color = Color.decode(hexColor)
        val red = ((color.red * (1 - amount) / 255 + amount) * 255).toInt()
        val green = ((color.green * (1 - amount) / 255 + amount) * 255).toInt()
        val blue = ((color.blue * (1 - amount) / 255 + amount) * 255).toInt()
        return "rgb($red, $green, $blue)"
    }
}
