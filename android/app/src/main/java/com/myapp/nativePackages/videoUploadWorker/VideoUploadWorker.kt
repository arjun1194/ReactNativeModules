package com.myapp.nativePackages.videoUploadWorker

import android.app.Notification
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import android.R

import com.myapp.MainApplication

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.*
import com.myapp.utils.InputStreamRequestBody
import com.myapp.utils.contentSchemeNameAndSize
import com.myapp.utils.getFileName
import com.myapp.utils.loge
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception

//TODO extract notification building logic from this file
class VideoUploadWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "VideoUploadWorker"

        //input data keys
        const val VIDEOS = "videos"
        // output data keys

        const val Progress = "Progress"
        const val VIDEO_DOWNLOADING_NOTIFICATION = 12312;

        private const val delayDuration = 1L

        val VIDEO_MP4_MEDIA_TYPE = "video/mp4".toMediaType()

    }

    private val videoUploadService = VideoUploadService.create()

    override suspend fun doWork(): Result {
        val notification = buildNotificationAndNotify()
        val videoUris = inputData.getStringArray(VIDEOS)!!
        val info = ForegroundInfo(VIDEO_DOWNLOADING_NOTIFICATION, notification)

        setForeground(info)

        return try {
            uploadVideos(videoUris)
        } catch (e: Exception) {
            e.loge(TAG)
            return Result.failure()
        }

    }

    private fun buildNotificationAndNotify(): Notification {
        val notification = createNotification("Videos Uploading", "Your uploads are in progress")
        notificationManager.notify(VIDEO_DOWNLOADING_NOTIFICATION, notification)
        return notification
    }

    private suspend fun uploadVideos(
        videoUris: Array<out String>
    ): Result {
        var currentProgress = 0
        for (i in videoUris.indices) {
            val uri: Uri = Uri.parse(videoUris[i])
            val result = uploadVideo(uri)
            if (result == -1) {
                return Result.failure()
            } else {
                val percentage = (i.toFloat() / videoUris.size.toFloat())
                setProgress(getProgressData(percentage))
            }
        }
        setProgress(getProgressData(100f))
        return Result.success()
    }

    private val notificationManager: NotificationManager
        get() {
            return applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

    private fun createNotification(title: String, body: String): Notification {
        return NotificationCompat.Builder(applicationContext, MainApplication.MainChannel)
            .setSmallIcon(R.drawable.arrow_down_float)
            .setColor(ContextCompat.getColor(applicationContext, R.color.holo_blue_dark))
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun buildFormDataFromContentUri(contentUri: Uri): MultipartBody.Part {
        val contentResolver = applicationContext.contentResolver
        val nameAndSize = contentUri.contentSchemeNameAndSize(contentResolver)
        val requestBody = InputStreamRequestBody(VIDEO_MP4_MEDIA_TYPE, contentResolver, contentUri)
        return MultipartBody.Part.createFormData("file", nameAndSize?.first?:"filename" + ".mp4", requestBody)
    }

    private fun buildFormDataFromFileUri(fileUri: Uri): MultipartBody.Part {
        val file = File(context.filesDir, getFileName(fileUri))
        val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
        return MultipartBody.Part.createFormData("file", file.name + ".mp4", requestBody)

    }
    private suspend fun uploadVideo(uri: Uri): Int {
        val filePart = buildFormDataFromFileUri(uri)
        val response = videoUploadService.uploadVideo(filePart)
        return if (response.isSuccessful) {
            Log.d(TAG, "uploadSingleVideo: ${response.body()!!}")
            response.body()!!.data.id
        } else {
            -1
        }
    }



    private fun getProgressData(progress: Float): Data {
        return workDataOf(Progress to progress)
    }




}