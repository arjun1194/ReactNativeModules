package com.myapp.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.myapp.data.VideoUploadService
import com.myapp.utils.io.contentSchemeNameAndSize
import com.myapp.utils.io.getFileName
import com.myapp.utils.networking.InputStreamRequestBody
import com.myapp.utils.notification.AppNotifications
import com.myapp.utils.notification.NotificationWrapper
import com.myapp.utils.workers.WorkerConstants
import com.myapp.utils.workers.WorkerConstants.Progress
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

//TODO extract notification building logic from this file
class VideoUploadWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "VideoUploadWorker"
        val VIDEO_MP4_MEDIA_TYPE = "video/mp4".toMediaType()

    }

    private val videoUploadService = VideoUploadService.create()
    private val notificationWrapper = NotificationWrapper(context)


    override suspend fun doWork(): Result {
        return try {

            val appNotification = AppNotifications.CREATE_POST_PROGRESS_NOTIFICATION
            val notificationBuilder = notificationWrapper.getNotification(appNotification)
            val info = ForegroundInfo(appNotification.id, notificationBuilder.build())

            val videoUris = inputData.getStringArray(WorkerConstants.VIDEO_UPLOAD_INPUT)!!
            setForeground(info)
            uploadVideos(videoUris)
        } catch (e: Exception) {
            Log.e(TAG, "doWork: ", e)
            return Result.failure()
        }

    }

    private suspend fun uploadVideos(
        videoUris: Array<out String>
    ): Result {
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

    private val isInputUriContent: Boolean
        get() = inputData.getBoolean(WorkerConstants.IS_CONTENT, true)


    private fun buildFormDataFromContentUri(contentUri: Uri): MultipartBody.Part {
        val contentResolver = applicationContext.contentResolver
        val nameAndSize = contentUri.contentSchemeNameAndSize(contentResolver)
        val requestBody = InputStreamRequestBody(
            VIDEO_MP4_MEDIA_TYPE,
            contentResolver,
            contentUri
        )
        return MultipartBody.Part.createFormData(
            "file",
            nameAndSize?.first ?: "filename" + ".mp4",
            requestBody
        )
    }

    private fun buildFormDataFromFileUri(fileUri: Uri): MultipartBody.Part {
        val file = File(context.filesDir, getFileName(fileUri))
        val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    private suspend fun uploadVideo(uri: Uri): Int {
        val filePart = if (isInputUriContent) buildFormDataFromContentUri(uri)
        else buildFormDataFromFileUri(uri)
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