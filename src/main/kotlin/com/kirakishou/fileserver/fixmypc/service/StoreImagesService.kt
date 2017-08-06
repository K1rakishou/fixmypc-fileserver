package com.kirakishou.fileserver.fixmypc.service

import org.springframework.web.multipart.MultipartFile

interface StoreImagesService {
    fun save(images: Array<MultipartFile>): Boolean
}