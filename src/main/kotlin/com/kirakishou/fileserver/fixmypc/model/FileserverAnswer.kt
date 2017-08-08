package com.kirakishou.fileserver.fixmypc.model

import com.fasterxml.jackson.annotation.JsonProperty

data class FileserverAnswer(@JsonProperty("status_code") val statusCode: Int,
                            @JsonProperty("bad_photo_names") val badPhotoNames: List<String>)