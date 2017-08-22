package com.kirakishou.fileserver.fixmypc.model

data class ServableImageInfo(val imageType: Int,
                             val ownerId: Long,
                             val folderName: String,
                             val imageName: String,
                             val isModifiedSince: Long)