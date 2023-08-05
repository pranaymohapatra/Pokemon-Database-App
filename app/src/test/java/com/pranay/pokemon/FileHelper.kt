package com.pranay.pokemon

import java.io.InputStreamReader

class FileHelper {
    fun readFile(fileName: String): String {
        val inputStream = FileHelper::class.java.getResourceAsStream(fileName)
        val stringBuilder = StringBuilder()
        InputStreamReader(inputStream, "UTF-8").readLines().forEach {
            stringBuilder.append(it)
        }
        return stringBuilder.toString()
    }
}