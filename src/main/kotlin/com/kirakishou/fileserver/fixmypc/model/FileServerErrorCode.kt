package com.kirakishou.fileserver.fixmypc.model

enum class FileServerErrorCode(val value: Int) {
    OK(0),
    COULD_NOT_STORE_ONE_OR_MORE_IMAGE(1),
    UNKNOWN_ERROR(2)
}