package com.myapp.nativePackages.videoUploadWorker

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.facebook.react.bridge.*
import com.myapp.nativePackages.videoUploadWorker.VideoUploadWorker.Companion.VIDEOS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.facebook.react.bridge.WritableMap

import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.myapp.nativePackages.videoUploadWorker.VideoCompressionWorker.Companion.INPUT_VIDEO_URI


class VideoUploadWorkerReactModule(private val reactContext: ReactApplicationContext): ContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "WorkManager"
    }

    private var liveData: LiveData<WorkInfo>? = null
    private var observer: Observer<WorkInfo>? = null
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
        removeLiveDataObserver()
    }

    @ReactMethod
    fun enqueueVideoUploads(videoUris: ReadableArray) {
        val inputData = getVideoCompressInputData(videoUris)

        val videoCompressRequest = OneTimeWorkRequestBuilder<VideoCompressionWorker>()
            .setInputData(inputData)
            .build()

        val videoUploadWorkRequest = OneTimeWorkRequestBuilder<VideoUploadWorker>().build()

        WorkManager
            .getInstance(reactContext)
            .beginWith(videoCompressRequest)
            .then(videoUploadWorkRequest)
            .enqueue()

        liveData = WorkManager.getInstance(reactContext)
            .getWorkInfoByIdLiveData(videoCompressRequest.id)

        observer = Observer<WorkInfo> { workInfo: WorkInfo? ->
            if (workInfo != null) {
                val progress = workInfo.progress.getFloat(VideoCompressionWorker.Progress, 0f)
                Log.d(TAG, "progress: $progress")
                val params = WritableNativeMap().apply {
                    putDouble("progress", progress.toDouble())
                }
                // Notify JS about Progress update
                sendEvent(params)
                //cancel subscription if Progress is complete
                if (progress == 100f) {
                    Log.d(TAG, "enqueueVideoUploads: Removed LiveDataObservers")
                    liveData?.removeObservers(lifecycleOwner)
                }
            }
        }
        removeLiveDataObserver()
    }

    private fun removeLiveDataObserver() {
        observer?.let {
            CoroutineScope(Dispatchers.Main).launch {
                liveData?.observe(lifecycleOwner, it)
            }
        }
    }

    private fun getVideoCompressInputData(videoUris: ReadableArray): Data =
        workDataOf(INPUT_VIDEO_URI to extractArgs(videoUris)[0])

    private fun getVideoUploadInputData(videoUris: ReadableArray): Data =
        workDataOf(VIDEOS to extractArgs(videoUris))

    private fun extractArgs(videoUris: ReadableArray): Array<String> =
         videoUris.toArrayList().map { it as String }.toTypedArray()


    private fun sendEvent(
        params: WritableMap?
    ) {
        reactContext
            .getJSModule(RCTDeviceEventEmitter::class.java)
            .emit(VIDEO_EVENT_NAME, params)
    }


    companion object {
        // REACT event listener keys
        private const val VIDEO_EVENT_NAME = "video_progress"
        // LOG tag
        private const val TAG = "VideoUploadWorkerReactM"
    }


}