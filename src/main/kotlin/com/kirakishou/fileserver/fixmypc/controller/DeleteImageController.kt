package com.kirakishou.fileserver.fixmypc.controller

import com.kirakishou.fileserver.fixmypc.log.FileLog
import com.kirakishou.fileserver.fixmypc.model.FileServerErrorCode
import com.kirakishou.fileserver.fixmypc.util.ServerUtils
import io.reactivex.Single
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.io.File
import javax.annotation.PostConstruct

@Controller
@RequestMapping
class DeleteImageController {

    @Value("\${server.images.path}")
    lateinit var imagesBasePath: String

    @Autowired
    lateinit var log: FileLog

    lateinit var malfunctionImagesDir: String

    @PostConstruct
    fun init() {
        malfunctionImagesDir = "$imagesBasePath\\malfunction_photos"
    }

    @RequestMapping(path = arrayOf("/v1/api/malfunction_image/{owner_id}/{malfunction_request_id}"), method = arrayOf(RequestMethod.DELETE))
    fun deleteImage(@PathVariable("owner_id") ownerId: Long,
                    @PathVariable("malfunction_request_id") malfunctionRequestId: String): Single<ResponseEntity<Int>> {

        return Single.just(ownerId)
                .map { id ->
                    val fullPath = "$malfunctionImagesDir\\$id\\$malfunctionRequestId"
                    val dirFile = File(fullPath)

                    if (!dirFile.exists()) {
                        return@map ResponseEntity(FileServerErrorCode.FILE_NOT_FOUND.value, HttpStatus.NOT_FOUND)
                    }

                    ServerUtils.deleteFolder(dirFile)

                    return@map ResponseEntity(FileServerErrorCode.OK.value, HttpStatus.OK)
                }
    }
}














































