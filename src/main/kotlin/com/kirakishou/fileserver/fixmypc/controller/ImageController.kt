package com.kirakishou.fileserver.fixmypc.controller

import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.model.Constant
import com.kirakishou.fileserver.fixmypc.model.FileserverAnswer
import com.kirakishou.fileserver.fixmypc.model.FileServerErrorCode
import com.kirakishou.fileserver.fixmypc.model.ForwardedImageInfo
import com.kirakishou.fileserver.fixmypc.util.ImageUtils
import com.kirakishou.fileserver.fixmypc.util.StringUtils
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.awt.Dimension
import java.io.File
import javax.annotation.PostConstruct

@Controller
@RequestMapping
class ImageController {

    @Value("\${server.images.path}")
    lateinit var imagesBasePath: String

    @Autowired
    lateinit var log: FileLog

    val imageFolderByType: Map<Int, String> by lazy {
        val map = HashMap<Int, String>()
        map.put(Constant.ImageType.IMAGE_TYPE_MALFUNCTION_PHOTO.value, "\\malfunction_photos\\")

        return@lazy map
    }

    @PostConstruct
    fun init() {
        for (imageType in Constant.ImageType.values()) {
            val path = imagesBasePath + imageFolderByType[imageType.value]

            val file = File(path)
            if (!file.exists()) {
                file.mkdir()
            }
        }
    }

    @RequestMapping(path = arrayOf("/v1/api/upload_image"), method = arrayOf(RequestMethod.POST))
    fun receiveImages(@RequestPart("images") uploadingFiles: List<MultipartFile>,
                      @RequestPart("images_info") imagesInfo: ForwardedImageInfo): Single<ResponseEntity<FileserverAnswer>> {

        return Single.just(uploadingFiles)
                .flatMap { photos ->
                    val badPhotos = arrayListOf<String>()
                    val size = imagesInfo.imageNewName.size

                    try {
                        for (index in 0 until size) {
                            val imageNewName = imagesInfo.imageNewName[index]
                            val imageOrigName = imagesInfo.imageOrigName[index]
                            val imageType = imagesInfo.imageType[index]
                            val ownerId = imagesInfo.ownerId[index]

                            val imageMultipartFile = photos.firstOrNull {
                                it.originalFilename == imageOrigName
                            }

                            if (imageMultipartFile == null) {
                                throw IllegalArgumentException("Couldn't find image with name $imageOrigName")
                            }

                            val currentFolderDirPath = imagesBasePath + imageFolderByType[imageType] + '\\' + ownerId.toString() + '\\'
                            val folderDir = File(currentFolderDirPath)
                            if (!folderDir.exists()) {
                                folderDir.mkdir()
                            }

                            val extension = StringUtils.extractExtension(imageOrigName)
                            val fullPath = currentFolderDirPath + imageNewName + "_o" + '.' + extension
                            val file = File(fullPath)

                            if (file.exists()) {
                                file.delete()
                            }

                            try {
                                //save original image
                                imageMultipartFile.transferTo(file)

                                //save medium version of the image
                                ImageUtils.resizeAndSaveImageOnDisk(file, Dimension(1536, 1536), "_m", currentFolderDirPath, imageNewName, extension)

                                //save small version of the image
                                ImageUtils.resizeAndSaveImageOnDisk(file, Dimension(512, 512), "_s", currentFolderDirPath, imageNewName, extension)
                            } catch (e: Exception) {
                                if (file.exists()) {
                                    file.delete()
                                }

                                badPhotos.add(imageMultipartFile.originalFilename)
                                log.e(e)
                            }
                        }

                    } catch (e: Exception) {
                        log.e(e)
                        return@flatMap Single.just(ResponseEntity.ok(FileserverAnswer(
                                FileServerErrorCode.UNKNOWN_ERROR.value, emptyList())))
                    }

                    if (badPhotos.isNotEmpty()) {
                        return@flatMap Single.just(ResponseEntity.ok(FileserverAnswer(
                                FileServerErrorCode.COULD_NOT_STORE_ONE_OR_MORE_IMAGE.value, badPhotos)))
                    } else {
                        return@flatMap Single.just(ResponseEntity.ok(FileserverAnswer(
                                FileServerErrorCode.OK.value, emptyList())))
                    }
                }
    }
}