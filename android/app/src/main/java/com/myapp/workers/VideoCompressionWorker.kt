package com.myapp.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.myapp.nativePackages.videoCompression.CompressionProgressObserver
import com.myapp.nativePackages.videoCompression.LightCompressor
import com.myapp.nativePackages.videoCompression.VideoCompressor
import com.myapp.utils.workers.WorkerConstants.IS_CONTENT
import com.myapp.utils.workers.WorkerConstants.Progress
import com.myapp.utils.workers.WorkerConstants.VIDEO_COMPRESS_INPUT
import com.myapp.utils.workers.WorkerConstants.VIDEO_UPLOAD_INPUT
import com.myapp.utils.io.getMediaPath
import com.myapp.utils.io.sizeInMB
import com.myapp.utils.notification.AppNotifications
import com.myapp.utils.notification.NotificationWrapper
import java.io.File

class VideoCompressionWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), CompressionProgressObserver {

    companion object {
        const val TAG = "VideoCompressionWorker"
        // minimum size in MB to start compression
        const val MINIMUM_COMPRESS_SIZE = 15
    }

    private val compressor: VideoCompressor = LightCompressor(context, this)
    private val notificationWrapper = NotificationWrapper(context)

    override suspend fun doWork(): Result {
        return try {

            val appNotification = AppNotifications.CREATE_POST_PROGRESS_NOTIFICATION
            val notificationBuilder = notificationWrapper.getNotification(appNotification)
            val info = ForegroundInfo(appNotification.id, notificationBuilder.build())
            setForeground(info)

            val fileUri = extractInputFileUri()
            val file = File(fileUri.path!!)
            return if (file.sizeInMB() > MINIMUM_COMPRESS_SIZE.toFloat()) {
                val destinationUri = compressor.compressVideo(fileUri)
                successResult(false,destinationUri.toString())
            } else {
                Log.i(TAG, "No need for compression bypassing...")
                successResult(true,inputData.getString(VIDEO_COMPRESS_INPUT))
            }

        } catch (e: Exception) {
            Log.e(TAG, "doWork: ", e)
            Result.failure(
                workDataOf(
                    VIDEO_UPLOAD_INPUT to inputData.getString(VIDEO_COMPRESS_INPUT)
                )
            )
        }
    }

    private fun successResult(isContent:Boolean, outputUri: String?): Result {
        return Result.success(
            workDataOf(
                IS_CONTENT to isContent,
                VIDEO_UPLOAD_INPUT to arrayOf(outputUri)
            )
        )
    }

    private fun extractInputFileUri(): Uri {
        val inputVideoUri = inputData.getString(VIDEO_COMPRESS_INPUT)
        val inputUri = Uri.parse(inputVideoUri)
        val uri = getMediaPath(context, inputUri)
        return Uri.parse(uri)
    }

    override fun onProgress(percent: Float) {
        Log.d(TAG, "onProgress: $percent")
        this.setProgressAsync(
            workDataOf(Progress to percent)
        )
    }


}