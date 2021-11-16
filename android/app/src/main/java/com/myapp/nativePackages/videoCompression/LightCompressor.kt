package com.myapp.nativePackages.videoCompression

import android.content.Context
import android.net.Uri
import android.util.Log
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.myapp.nativePackages.videoUploadWorker.UnableToCompressException
import com.myapp.utils.workers.WorkerConstants
import com.myapp.workers.VideoCompressionWorker
import com.myapp.utils.io.saveVideoFile
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.properties.Delegates
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor as Compressor

class LightCompressor(
    private val context: Context,
    private val progressObserver: CompressionProgressObserver
) : VideoCompressor {
    companion object {
        const val TAG = VideoCompressionWorker.TAG
    }
    var startTime by Delegates.notNull<Long>()
    var endTime by Delegates.notNull<Long>()
    override suspend fun compressVideo(uri: Uri): Uri {

        val file = context.saveVideoFile(WorkerConstants.TEMP_FILENAME)

        return suspendCancellableCoroutine {
            Compressor.start(
                context = context,
                srcUri = uri,
                // srcPath = path,
                destPath = file.path,
                listener = object : CompressionListener {
                    override fun onProgress(percent: Float) {
                        progressObserver.onProgress(percent)
                    }

                    override fun onStart() {
                        startTime = System.nanoTime()
                    }

                    override fun onSuccess() {
                        endTime = System.nanoTime()
                        Log.i(TAG, "Compression took ${(endTime - startTime)/1000000000} Seconds!")
                        it.resume(Uri.fromFile(file))
                    }

                    override fun onFailure(failureMessage: String) {
                        it.resumeWithException(UnableToCompressException(failureMessage))
                    }

                    override fun onCancelled() {
                        Log.i(TAG, "Video Compression was cancelled!")
                    }

                },
                configureWith = Configuration(
                    quality = VideoQuality.VERY_LOW,
                    frameRate = 24,
                    isMinBitrateCheckEnabled = false,
                )


            )

            it.invokeOnCancellation {
                Compressor.cancel()
            }
        }

    }
}
