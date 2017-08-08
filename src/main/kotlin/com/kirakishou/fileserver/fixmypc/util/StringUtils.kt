package com.kirakishou.fileserver.fixmypc.util

object StringUtils {

    fun extractExtension(inputStr: String): String {
        val sb = StringBuilder()
        val strLen = inputStr.length - 1

        for (index in (strLen downTo 0)) {
            if (inputStr[index] == '.') {
                break
            }

            sb.insert(0, inputStr[index])
        }

        return sb.toString()
    }
}