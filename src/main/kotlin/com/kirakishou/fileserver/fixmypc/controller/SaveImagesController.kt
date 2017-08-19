package com.kirakishou.fileserver.fixmypc.controller

import com.kirakishou.fileserver.fixmypc.model.DistributedImage
import com.kirakishou.fileserver.fixmypc.model.FileServerAnswer
import com.kirakishou.fileserver.fixmypc.model.FileServerErrorCode
import com.kirakishou.fileserver.fixmypc.service.SaveImagesService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping
class SaveImagesController {

    @Autowired
    lateinit var saveImagesService: SaveImagesService

    @RequestMapping(path = arrayOf("/v1/api/upload_image"), method = arrayOf(RequestMethod.POST))
    fun saveImages(@RequestPart("images") uploadingFiles: List<MultipartFile>,
                   @RequestPart("images_info") distributedImage: DistributedImage): Single<ResponseEntity<FileServerAnswer>> {

        return Single.just(uploadingFiles)
                .map { images ->
                    val result = saveImagesService.save(images, distributedImage)

                    when (result) {
                        is SaveImagesService.Result.Ok -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.OK.value, emptyList()))
                        }

                        is SaveImagesService.Result.CouldNotStoreOneOrMoreImages -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.COULD_NOT_STORE_ONE_OR_MORE_IMAGE.value, result.badPhotos))
                        }

                        is SaveImagesService.Result.UnknownError -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.UNKNOWN_ERROR.value, emptyList()))
                        }

                        else -> throw IllegalArgumentException("Unknown result")
                    }
                }
    }
}