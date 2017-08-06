package com.kirakishou.fileserver.fixmypc.service

import org.springframework.web.multipart.MultipartFile

class StoreImagesServiceImpl : StoreImagesService {

    override fun save(images: Array<MultipartFile>): Boolean {
        return true
    }
}