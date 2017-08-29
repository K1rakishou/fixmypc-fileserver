package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.model.Constant
import com.kirakishou.fileserver.fixmypc.model.DistributedImage
import com.kirakishou.fileserver.fixmypc.util.ImageUtils
import com.kirakishou.fileserver.fixmypc.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.Dimension
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

@Component
class SaveImageServiceImpl : SaveImageService {

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

    override fun save(image: MultipartFile, distributedImage: DistributedImage): SaveImageService.Post.Result {
        val badPhotos = arrayListOf<String>()

        try {
            val imageNewName = distributedImage.imageNewName
            val imageOrigName = distributedImage.imageOrigName
            val imageType = distributedImage.imageType
            val ownerId = distributedImage.ownerId
            val malfunctionRequestId = distributedImage.malfunctionRequestId

            val fullPathToDir = "${imageFolderByType[imageType]}\\$ownerId\\$malfunctionRequestId\\"
            val dirFile = File(fullPathToDir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }

            val extension = StringUtils.extractExtension(imageOrigName)
            val fullPath = fullPathToDir + imageNewName + '.' + extension
            val file = File(fullPath)

            log.d("fullPath = $fullPath")
            log.d("Saving a file ${image.originalFilename}")

            //delete old image if it exists
            if (file.exists()) {
                file.delete()
            }

            try {
                //copy original image
                image.transferTo(file)

                //save large version of the image
                ImageUtils.resizeAndSaveImageOnDisk(file, Dimension(2560, 2560), "_l", fullPathToDir, imageNewName, extension)

                //save medium version of the image
                ImageUtils.resizeAndSaveImageOnDisk(file, Dimension(1536, 1536), "_m", fullPathToDir, imageNewName, extension)

                //save small version of the image
                ImageUtils.resizeAndSaveImageOnDisk(file, Dimension(512, 512), "_s", fullPathToDir, imageNewName, extension)

                //remove original image
                if (file.exists()) {
                    file.delete()
                }

            } catch (e: Exception) {
                log.e(e)

                if (file.exists()) {
                    file.delete()
                }

                badPhotos.add(image.originalFilename)
            }

        } catch (e: Exception) {
            log.e(e)
            return SaveImageService.Post.Result.UnknownError()
        }

        if (badPhotos.isNotEmpty()) {
            return SaveImageService.Post.Result.CouldNotStoreOneOrMoreImages(badPhotos)
        }

        return SaveImageService.Post.Result.Ok()
    }
}