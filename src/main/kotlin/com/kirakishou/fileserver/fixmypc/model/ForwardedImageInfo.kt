package com.kirakishou.fileserver.fixmypc.model

import com.fasterxml.jackson.annotation.JsonProperty

class ForwardedImageInfo {
    @JsonProperty("image_orig_name") val imageOrigName = arrayListOf<String>()
    @JsonProperty("image_type") val imageType = arrayListOf<Int>()
    @JsonProperty("image_name") val imageNewName = arrayListOf<String>()
}