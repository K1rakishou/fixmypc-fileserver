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
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

@Component
class SaveImagesServiceImpl : SaveImagesService {

    @Value("\${server.images.path}")
    lateinit var imagesBasePath: String

    @Autowired
    lateinit var log: FileLog

    val imageFolderByType: Map<Int, String> by lazy {
        val map = ConcurrentHashMap<Int, String>()
        map.put(Constant.ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value, "${imagesBasePath}\\malfunction_photos")

        return@lazy map
    }

    @PostConstruct
    fun init() {
        for (imageType in Constant.ImageType.values()) {
            val file = File(imageFolderByType[imageType.value])
            if (!file.exists()) {
                file.mkdir()
            }
        }
    }

    override fun save(images: List<MultipartFile>, distributedImage: DistributedImage): SaveImagesService.Result {
        val badPhotos = arrayListOf<String>()

        try {
            val imageNewName = distributedImage.imageNewName
            val imageOrigName = distributedImage.imageOrigName
            val imageType = distributedImage.imageType
            val ownerId = distributedImage.ownerId
            val malfunctionRequestId = distributedImage.malfunctionRequestId

            val imageMultipartFile = images.firstOrNull {
                it.originalFilename == imageOrigName
            } ?: throw IllegalArgumentException("Couldn't find image with name $imageOrigName")

            val fullPathToDir = "${imageFolderByType[imageType]}\\$ownerId\\$malfunctionRequestId\\"
            val dirFile = File(fullPathToDir)
            if (!dirFile.exists()) {
                dirFile.mkdirs()
            }

            val extension = StringUtils.extractExtension(imageOrigName)
            val fullPath = fullPathToDir + imageNewName + '.' + extension
            val file = File(fullPath)

            log.d("fullPath = $fullPath")
            log.d("Saving a file ${imageMultipartFile.originalFilename}")

            //delete old image if it exists
            if (file.exists()) {
                file.delete()
            }

            try {
                //copy original image
                imageMultipartFile.transferTo(file)

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

            } catch (e: IOException) {
                log.e(e)

                if (file.exists()) {
                    file.delete()
                }

                badPhotos.add(imageMultipartFile.originalFilename)
            }

        } catch (e: Exception) {
            log.e(e)
            return SaveImagesService.Result.UnknownError()
        }

        if (badPhotos.isNotEmpty()) {
            return SaveImagesService.Result.CouldNotStoreOneOrMoreImages(badPhotos)
        }

        return SaveImagesService.Result.Ok()
    }
}