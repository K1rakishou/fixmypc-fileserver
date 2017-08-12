package com.kirakishou.fileserver.fixmypc.service

import org.springframework.stereotype.Component
import java.io.File

@Component
class DiskSpaceServiceImpl : DiskSpaceService {
    private lateinit var imageBasePath: File
    private var diskSpaceOnePercent: Long = 0L
    private var diskFreeSpace: Long = 0

    override fun init(imgBasePath: String) {
        this.imageBasePath = File(imgBasePath)
        this.diskSpaceOnePercent = imageBasePath.totalSpace / 100
    }

    @Synchronized
    override fun isEnoughDiskSpace(): Boolean {
        return diskSpaceOnePercent < diskFreeSpace
    }

    @Synchronized
    override operator fun minusAssign(fileSize: Long) {
        diskFreeSpace -= fileSize
    }
}