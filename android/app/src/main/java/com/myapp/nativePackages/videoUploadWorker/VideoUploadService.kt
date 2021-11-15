package com.myapp.nativePackages.videoUploadWorker

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface VideoUploadService {
    @Multipart
    @POST("upload")
    suspend fun uploadVideo(@Part filePart: MultipartBody.Part): Response<VideoUploadResponse>

    companion object {
        fun create() : VideoUploadService {

            val logger = HttpLoggingInterceptor().apply {
                level = BASIC
            }

            val client = OkHttpClient
                .Builder()
                .addInterceptor(logger)
                .build()

           return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/")
                .client(client)
                .build()
                .create(VideoUploadService::class.java)
        }
    }
}