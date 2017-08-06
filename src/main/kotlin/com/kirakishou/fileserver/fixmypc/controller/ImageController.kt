package com.kirakishou.fileserver.fixmypc.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono

@Controller
@RequestMapping
class ImageController {

    @RequestMapping(path = arrayOf("/v1/api/upload_image"), method = arrayOf(RequestMethod.POST))
    fun receiveImages(@RequestPart("photos") uploadingFiles: Array<MultipartFile>): Mono<ResponseEntity<String>> {
        return Mono.just(uploadingFiles)
                .flatMap { photos ->


                    return@flatMap Mono.just(ResponseEntity.ok("123"))
                }
    }
}