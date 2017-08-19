package com.kirakishou.fileserver.fixmypc.model

object Constant {
    enum class ImageType(val value: Int) {
        IMAGE_TYPE_MALFUNCTION_PHOTO(0)
    }

    object SerializedNames {
        const val ERROR_CODE = "error_code"
        const val BAD_PHOTO_NAMES = "bad_photo_names"

        const val IMAGE_ORIGINAL_NAME = "image_orig_name"
        const val IMAGE_TYPE = "image_type"
        const val IMAGE_NAME = "image_name"
        const val OWNER_ID = "owner_id"
        const val MALFUNCTION_REQUEST_ID = "malfunction_request_id"
    }

}