package com.kirakishou.fileserver.fixmypc.service

interface DeleteImagesService {

    interface Result {
        class Ok : Result
        class NotFound : Result
    }

    fun deleteImages(ownerId: Long, malfunctionRequestId: String): Result
}