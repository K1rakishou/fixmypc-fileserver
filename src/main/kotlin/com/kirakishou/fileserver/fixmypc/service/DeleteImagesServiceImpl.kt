package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.util.ServerUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct

@Component
class DeleteImagesServiceImpl : DeleteImagesService {

    @Value("\${server.images.path}")
    private lateinit var imagesBasePath: String

    private lateinit var malfunctionImagesDir: String

    @Autowired
    private lateinit var log: FileLog

    @PostConstruct
    fun init() {
        malfunctionImagesDir = "$imagesBasePath\\malfunction_images"
    }

    override fun deleteImages(ownerId: Long, malfunctionRequestId: String): DeleteImagesService.Delete.Result {
        val fullPath = "$malfunctionImagesDir\\$ownerId\\$malfunctionRequestId"
        val dirFile = File(fullPath)

        if (!dirFile.exists()) {
            log.d("directory ${dirFile.absolutePath} does not exist")
            return DeleteImagesService.Delete.Result.NotFound()
        }

        log.d("Deleting directory ${dirFile.absolutePath}")
        ServerUtils.deleteFolder(dirFile)

        return DeleteImagesService.Delete.Result.Ok()
    }
}