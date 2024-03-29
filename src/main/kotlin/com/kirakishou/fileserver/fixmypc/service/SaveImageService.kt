package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.model.DistributedImage
import org.springframework.web.multipart.MultipartFile

interface SaveImageService {

    interface Post {
        interface Result {
            class Ok : Result
            class UnknownError : Result
            class CouldNotStoreOneOrMoreImages(val badPhotos: List<String>) : Result
        }
    }

    fun save(image: MultipartFile, distributedImage: DistributedImage): Post.Result
}