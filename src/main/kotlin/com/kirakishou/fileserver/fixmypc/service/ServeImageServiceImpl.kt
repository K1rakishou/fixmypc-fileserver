package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.controller.ImageController
import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.model.Constant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

@Component
class ServeImageServiceImpl : ServeImageService {

    @Value("\${server.images.path}")
    private lateinit var imagesBasePath: String

    @Autowired
    private lateinit var log: FileLog

    val imageFolderByType: Map<Int, String> by lazy {
        val map = ConcurrentHashMap<Int, String>()
        map.put(Constant.ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value, "${imagesBasePath}\\malfunction_images")

        return@lazy map
    }

    @PostConstruct
    fun init() {
        for (imageType in Constant.ImageType.values()) {
            val file = File(imageFolderByType[imageType.value])
            if (!file.exists()) {
                file.mkdirs()
            }
        }
    }


    override fun serveImage(servableImageInfo: ImageController.ServableImageInfo): ServeImageService.Result {
        val imageType = servableImageInfo.imageType
        val imageName = servableImageInfo.imageName
        val ownerId = servableImageInfo.ownerId
        val folderName = servableImageInfo.folderName
        val isModifiedSince = servableImageInfo.isModifiedSince

        val fullPathToImage = "${imageFolderByType[imageType]}\\$ownerId\\$folderName\\$imageName"
        val file = File(fullPathToImage)

        if (file.lastModified() < isModifiedSince) {
            return ServeImageService.Result.NotModified()
        }

        if (!file.exists() || !file.isFile) {
            return ServeImageService.Result.NotFound()
        }

        return ServeImageService.Result.Ok(file.lastModified(), file.inputStream())
    }
}



































