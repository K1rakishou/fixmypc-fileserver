package com.kirakishou.fileserver.fixmypc.service

interface DiskSpaceService {
    fun init(imgBasePath: String, checkTimeInterval: Long)
    fun isEnoughDiskSpace(): Boolean
}