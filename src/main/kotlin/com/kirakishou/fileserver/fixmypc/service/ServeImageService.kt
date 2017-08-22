package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.controller.ImageController
import java.io.InputStream

interface ServeImageService {

    interface Result {
        class Ok(val lastModified: Long, val inputStream: InputStream) : Result
        class NotFound : Result
        class NotModified : Result
    }

    fun serveImage(servableImageInfo: ImageController.ServableImageInfo): Result
}