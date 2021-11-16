package com.myapp.data.model.response

import androidx.annotation.Keep

@Keep
data class VideoUploadResponse(
    val status: String,
    val data: Data,
) {
    data class Data(
        val id: Int,
        val title: String,
        val description: String
    )
}