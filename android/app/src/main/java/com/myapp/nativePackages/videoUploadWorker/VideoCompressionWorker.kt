package com.myapp.nativePackages.videoUploadWorker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.myapp.utils.getMediaPath
import com.myapp.utils.logd
import com.myapp.utils.loge
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class VideoCompressionWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val TAG = "VideoCompressionWorker"
        const val INPUT_VIDEO_URI = "input_data_uri"
        const val OUTPUT_VIDEO_URI = "videos"
        const val Progress = "progress"
    }


    override suspend fun doWork(): Result {
        return try {
            val startTime = System.nanoTime();

            inputData.getString(INPUT_VIDEO_URI)
            val inputVideoUri = inputData.getString(INPUT_VIDEO_URI).logd(TAG, "inputVideoUri")
            val inputUri = Uri.parse(inputVideoUri)
            val uri = getMediaPath(context, inputUri).logd(TAG, "getMediaPathUri")
            val fileUri = Uri.parse(uri)
            val destinationUri = compressVideo(fileUri).logd(TAG, "destinationUri")
            val endTime = System.nanoTime()
            Log.i(
                TAG,
                "It took ${(endTime - startTime) / 1000000000} seconds to compress the video"
            )
            Result.success(workDataOf(OUTPUT_VIDEO_URI to arrayOf(destinationUri.toString())))
        } catch (e: Exception) {
            e.loge(TAG)
            Result.failure(workDataOf(OUTPUT_VIDEO_URI to inputData.getString(INPUT_VIDEO_URI)))
        }
    }



    private suspend fun compressVideo(uri: Uri): Uri {
        val videoFileName = "temp"

        val desFile = saveVideoFile(videoFileName)
        val streamableFile = saveVideoFile(videoFileName)
        return suspendCancellableCoroutine {
            VideoCompressor.start(
                context = applicationContext,
                srcUri = uri,
                // srcPath = path,
                destPath = desFile.path,
                streamableFile = streamableFile.path,
                listener = object : CompressionListener {
                    override fun onProgress(percent: Float) {
                        // Update UI with progress value
                        this@VideoCompressionWorker.setProgressAsync(workDataOf(Progress to percent))
                    }

                    override fun onStart() {
                        Log.d(TAG, "onStart: compression started")
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: Destination file is ${desFile.absolutePath}")
                        it.resume(Uri.fromFile(desFile))
                    }

                    override fun onFailure(failureMessage: String) {
                        it.resumeWithException(UnableToCompressException(failureMessage))
                    }

                    override fun onCancelled() {
                        Log.d(TAG, "onCancelled: Video Compression was cancelled")

                    }

                },
                configureWith = Configuration(
                    quality = VideoQuality.VERY_LOW,
                    frameRate = 24,
                    isMinBitrateCheckEnabled = true,
                )


            )

            it.invokeOnCancellation {
                VideoCompressor.cancel()
            }
        }


    }


    private fun saveVideoFile(videoFileName: String): File {
        val path = applicationContext.filesDir
        val desFile = File(path, videoFileName)

        if (desFile.exists())
            desFile.delete()

        try {
            desFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return desFile.logd(TAG,"destinationFile")
    }

}