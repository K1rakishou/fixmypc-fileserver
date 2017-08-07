package com.kirakishou.fileserver.fixmypc.controller

import com.kirakishou.fileserver.fixmypc.model.Constant
import com.kirakishou.fileserver.fixmypc.model.ForwardedImageInfo
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct

@Controller
@RequestMapping
class ImageController {

    @Value("\${server.images.path}")
    lateinit var imagesBasePath: String

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
                      @RequestPart("images_info") imagesInfo: ForwardedImageInfo): Single<ResponseEntity<String>> {

        return Single.just(uploadingFiles)
                .flatMap { photos ->
                    val badPhotos = arrayListOf<String>()
                    val size = imagesInfo.imageNewName.size

                    for (index in 0 until size) {
                        val imageNewName = imagesInfo.imageNewName[index]
                        val imageOrigName = imagesInfo.imageOrigName[index]
                        val imageType = imagesInfo.imageType[index]

                        val imageMultipartFile = uploadingFiles.firstOrNull {
                            it.originalFilename == imageOrigName
                        }

                        if (imageMultipartFile == null) {
                            throw IllegalArgumentException("Couldn't find image with name $imageOrigName")
                        }

                        val fullPath = imagesBasePath + imageFolderByType[imageType] + '\\' + imageNewName
                        val file = File(fullPath)

                        if (file.exists()) {
                            file.delete()
                        }

                        try {
                            imageMultipartFile.transferTo(file)
                        } catch (e: Exception) {
                            badPhotos.add(imageMultipartFile.originalFilename)
                            e.printStackTrace()
                        }
                    }

                    return@flatMap Single.just(ResponseEntity.ok("123"))
                }
    }
}