package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.util.ServerUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct

@Component
class DeleteImagesServiceImpl : DeleteImagesService {

    @Value("\${server.images.path}")
    private lateinit var imagesBasePath: String

    private lateinit var malfunctionImagesDir: String

    @PostConstruct
    fun init() {
        malfunctionImagesDir = "$imagesBasePath\\malfunction_images"
    }

    override fun deleteImages(ownerId: Long, malfunctionRequestId: String): DeleteImagesService.Result {
        val fullPath = "$malfunctionImagesDir\\$ownerId\\$malfunctionRequestId"
        val dirFile = File(fullPath)

        if (!dirFile.exists()) {
            return DeleteImagesService.Result.NotFound()
        }

        ServerUtils.deleteFolder(dirFile)

        return DeleteImagesService.Result.Ok()
    }
}