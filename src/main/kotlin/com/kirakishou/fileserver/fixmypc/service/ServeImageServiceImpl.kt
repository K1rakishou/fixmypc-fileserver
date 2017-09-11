package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.model.Constant
import com.kirakishou.fileserver.fixmypc.model.ServableImageInfo
import com.kirakishou.fileserver.fixmypc.util.StringUtils
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

    override fun serveImage(servableImageInfo: ServableImageInfo): ServeImageService.Get.Result {
        val imageType = servableImageInfo.imageType
        val imageName = servableImageInfo.imageName
        val ownerId = servableImageInfo.ownerId
        val folderName = servableImageInfo.folderName
        val extension = StringUtils.extractExtension(imageName)
        val imageNameWithoutExtension = imageName.substring(0, imageName.length - extension.length - 1)

        val size = when (servableImageInfo.size) {
            "large" -> "l"
            "medium" -> "m"
            "small" -> "s"
            else -> "m"
        }

        val fullPathToImage = "${imageFolderByType[imageType]}\\$ownerId\\$folderName\\${imageNameWithoutExtension}_$size.$extension"
        val file = File(fullPathToImage)

        if (!file.exists() || !file.isFile) {
            return ServeImageService.Get.Result.NotFound()
        }

        return ServeImageService.Get.Result.Ok(file.inputStream())
    }
}



































