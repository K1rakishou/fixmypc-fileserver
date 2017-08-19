package com.kirakishou.fileserver.fixmypc.util

import java.io.File

object ServerUtils {

    fun getTimeFast(): Long {
        return System.currentTimeMillis()
    }

    fun deleteFolder(folder: File) {
        val files = folder.listFiles()

        if (files != null) {
            for (f in files) {
                if (f.isDirectory) {
                    deleteFolder(f)
                } else {
                    f.delete()
                }
            }
        }

        folder.delete()
    }
}