package com.kirakishou.fileserver.fixmypc.service

import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit

@Component
class DiskSpaceServiceImpl : DiskSpaceService {
    private lateinit var imageBasePath: File
    private var diskSpaceOnePercent: Long = 0L
    private var diskFreeSpace: Long = 0
    private var lastTimeCheck: Long = 0
    private var checkTimeInterval: Long = 0

    override fun init(imgBasePath: String, checkTimeInterval: Long) {
        this.imageBasePath = File(imgBasePath)
        this.diskSpaceOnePercent = imageBasePath.totalSpace / 100
        this.checkTimeInterval = TimeUnit.SECONDS.toMillis(checkTimeInterval)
    }

    @Synchronized
    override fun isEnoughDiskSpace(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastTimeCheck > checkTimeInterval) {
            lastTimeCheck = now
            diskFreeSpace = imageBasePath.freeSpace
        }

        return diskSpaceOnePercent > diskFreeSpace
    }
}