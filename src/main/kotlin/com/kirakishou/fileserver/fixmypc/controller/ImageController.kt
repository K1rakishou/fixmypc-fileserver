package com.kirakishou.fileserver.fixmypc.controller

import io.reactivex.Single
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping
class ImageController {

    @RequestMapping(path = arrayOf("/v1/api/upload_image"), method = arrayOf(RequestMethod.POST))
    fun receiveImages(@RequestPart("images") uploadingFiles: Array<MultipartFile>): Single<ResponseEntity<String>> {
        return Single.just(uploadingFiles)
                .flatMap { photos ->

                    for (photo in photos) {
                        System.out.println("photoName: ${photo.originalFilename}")
                    }

                    return@flatMap Single.just(ResponseEntity.ok("123"))
                }
    }
}