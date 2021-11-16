package com.myapp.nativePackages.videoUploadWorker

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.facebook.react.bridge.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.facebook.react.bridge.WritableMap

import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.myapp.utils.reactNative.EventTimeMonitor
import com.myapp.utils.workers.WorkProgressManager
import com.myapp.utils.workers.WorkerConstants
import com.myapp.workers.*
import com.myapp.utils.workers.WorkerConstants.VIDEO_COMPRESS_INPUT


class VideoUploadWorkerReactModule(private val reactContext: ReactApplicationContext) :
    ContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "WorkManager"
    }

    private val lifecycleOwner: AppCompatActivity by lazy {
        ((context as ReactContext).currentActivity as AppCompatActivity)
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // start Java listeners
        // when coming back to a react screen we would need to call this to
        // resume listening to that live data
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // cancel the livedata subscription

    }

    @ReactMethod
    fun enqueueVideoUploads(videoUris: ReadableArray) {

        val (videoCompressRequest, videoUploadWorkRequest) = enqueueVideoRequest(videoUris)


        CoroutineScope(Dispatchers.Main).launch {
            val liveData = WorkProgressManager(listOf(videoCompressRequest, videoUploadWorkRequest)).initialize(
                    reactContext.currentActivity as AppCompatActivity
                )
            // call this only from the main thread
            liveData.observe(lifecycleOwner) {
                val params = WritableNativeMap().apply {
                    putDouble("progress", it.toDouble())
                }
                Log.i(TAG, "progress --> $it")
                // Notify JS about Progress update
                if(EventTimeMonitor.shouldSendEvent()) {
                    sendEvent(params)
                }
            }
        }
    }

    private fun enqueueVideoRequest(videoUris: ReadableArray): Pair<OneTimeWorkRequest, OneTimeWorkRequest> {
        val inputData = getVideoCompressInputData(videoUris)
        val videoCompressRequest = OneTimeWorkRequestBuilder<VideoCompressionWorker>().setInputData(inputData).build()
        val videoUploadWorkRequest = OneTimeWorkRequestBuilder<VideoUploadWorker>().build()
        val cleanupWorkerReq = OneTimeWorkRequestBuilder<CleanupWorker>().build()

        WorkManager.getInstance(reactContext).beginWith(videoCompressRequest)
            .then(videoUploadWorkRequest)
            .then(cleanupWorkerReq)
            .enqueue()
        return Pair(videoCompressRequest, videoUploadWorkRequest)
    }

    private fun getVideoCompressInputData(videoUris: ReadableArray): Data {
        return workDataOf(VIDEO_COMPRESS_INPUT to extractArgs(videoUris)[0])
    }

    private fun getVideoUploadInputData(videoUris: ReadableArray): Data {
        return workDataOf(WorkerConstants.VIDEO_UPLOAD_INPUT to extractArgs(videoUris))
    }

    private fun extractArgs(videoUris: ReadableArray): Array<String> =
        videoUris.toArrayList().map { it as String }.toTypedArray()


    private fun sendEvent(
        params: WritableMap?
    ) {
        reactContext
            .getJSModule(RCTDeviceEventEmitter::class.java)
            .emit(VIDEO_PROGRESS, params)
    }


    companion object {
        // REACT event listener keys
        private const val VIDEO_PROGRESS = "video_progress"

        // LOG tag
        private const val TAG = "VideoUploadWorkerReactM"
    }


}