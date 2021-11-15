package com.myapp.nativePackages.videoUploadWorker

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