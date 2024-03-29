package com.kirakishou.fileserver.fixmypc.model

enum class FileServerErrorCode(val value: Int) {
    UNKNOWN_ERROR(-1),
    OK(0),
    COULD_NOT_STORE_IMAGE(1),
    FILE_NOT_FOUND(2)
}