package com.kirakishou.fileserver.fixmypc.service

interface DiskSpaceService {
    fun init(imgBasePath: String)
    fun isEnoughDiskSpace(): Boolean
    operator fun minusAssign(fileSize: Long)
}