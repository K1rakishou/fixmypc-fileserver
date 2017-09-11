package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.model.ServableImageInfo
import java.io.InputStream

interface ServeImageService {

    interface Get {
        interface Result {
            class Ok(val inputStream: InputStream) : Result
            class NotFound : Result
            class NotModified : Result
        }
    }

    fun serveImage(servableImageInfo: ServableImageInfo): Get.Result
}