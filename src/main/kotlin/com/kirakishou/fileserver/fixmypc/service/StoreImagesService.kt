package com.kirakishou.fileserver.fixmypc.service

import com.kirakishou.fileserver.fixmypc.model.ForwardedImageInfo
import org.springframework.web.multipart.MultipartFile

interface StoreImagesService {

    interface Result {
        class Ok: Result
        class UnknownError: Result
        class CouldNotStoreOneOrMoreImages(val badPhotos: List<String>): Result
    }

    fun save(images: List<MultipartFile>, imagesInfo: ForwardedImageInfo): Result
}