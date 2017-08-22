package com.kirakishou.fileserver.fixmypc.controller

import com.kirakishou.fileserver.fixmypc.model.DistributedImage
import com.kirakishou.fileserver.fixmypc.model.FileServerAnswer
import com.kirakishou.fileserver.fixmypc.model.FileServerErrorCode
import com.kirakishou.fileserver.fixmypc.model.ServableImageInfo
import com.kirakishou.fileserver.fixmypc.service.DeleteImagesService
import com.kirakishou.fileserver.fixmypc.service.SaveImageService
import com.kirakishou.fileserver.fixmypc.service.ServeImageService
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping
class ImageController {

    @Autowired
    lateinit var saveImageService: SaveImageService

    @Autowired
    lateinit var deleteImagesService: DeleteImagesService

    @Autowired
    lateinit var serveImageService: ServeImageService

    @RequestMapping(path = arrayOf("/v1/api/malfunction_image"), method = arrayOf(RequestMethod.POST))
    fun saveImage(@RequestPart("image") uploadingFile: MultipartFile,
                  @RequestPart("image_info") distributedImage: DistributedImage): Single<ResponseEntity<FileServerAnswer>> {

        return Single.just(uploadingFile)
                .map { image ->
                    val result = saveImageService.save(image, distributedImage)

                    when (result) {
                        is SaveImageService.Post.Result.Ok -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.OK.value, emptyList()))
                        }

                        is SaveImageService.Post.Result.CouldNotStoreOneOrMoreImages -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.COULD_NOT_STORE_IMAGE.value, result.badPhotos))
                        }

                        is SaveImageService.Post.Result.UnknownError -> {
                            return@map ResponseEntity.ok(FileServerAnswer(FileServerErrorCode.UNKNOWN_ERROR.value, emptyList()))
                        }

                        else -> throw IllegalArgumentException("Unknown result")
                    }
                }
    }

    @RequestMapping(path = arrayOf("/v1/api/malfunction_image/{owner_id}/{m_request_id}"), method = arrayOf(RequestMethod.DELETE))
    fun deleteImages(@PathVariable("owner_id") ownerId: Long,
                     @PathVariable("m_request_id") malfunctionRequestId: String): Single<ResponseEntity<Int>> {

        return Single.just(ownerId)
                .map { id ->
                    val result = deleteImagesService.deleteImages(id, malfunctionRequestId)

                    when (result) {
                        is DeleteImagesService.Delete.Result.Ok -> {
                            return@map ResponseEntity(FileServerErrorCode.OK.value, HttpStatus.OK)
                        }

                        is DeleteImagesService.Delete.Result.NotFound -> {
                            return@map ResponseEntity(FileServerErrorCode.FILE_NOT_FOUND.value, HttpStatus.NOT_FOUND)
                        }

                        else -> throw IllegalArgumentException("Unknown result")
                    }
                }
    }

    @RequestMapping(path = arrayOf("/v1/api/malfunction_image/{is_modified_since}/{image_type}/{owner_id}/{folder_name}/{image_name:.+}"),
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf(MediaType.IMAGE_PNG_VALUE))
    fun serverImage(@PathVariable("image_type") imageType: Int,
                    @PathVariable("owner_id") ownerId: Long,
                    @PathVariable("folder_name") folderName: String,
                    @PathVariable("image_name") imageName: String,
                    @PathVariable("is_modified_since") isModifiedSince: Long): Single<ResponseEntity<Resource>> {

        return Single.just(ServableImageInfo(imageType, ownerId, folderName, imageName, isModifiedSince))
                .map { sii ->
                    val result = serveImageService.serveImage(sii)

                    when (result) {
                        is ServeImageService.Get.Result.Ok -> {
                            return@map ResponseEntity
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.IMAGE_PNG)
                                    .contentLength(result.inputStream.available().toLong())
                                    .lastModified(result.lastModified)
                                    .body<Resource>(InputStreamResource(result.inputStream))
                        }

                        is ServeImageService.Get.Result.NotModified -> {
                            return@map ResponseEntity<Resource>(null, HttpStatus.NOT_MODIFIED)
                        }

                        is ServeImageService.Get.Result.NotFound -> {
                            return@map ResponseEntity<Resource>(null, HttpStatus.NOT_FOUND)
                        }

                        else -> throw IllegalArgumentException("Unknown result")
                    }
                }

    }
}







































