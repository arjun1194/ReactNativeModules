package com.myapp.nativePackages.videoCompression

import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class CallbackMethodModule(context: ReactApplicationContext?) :
    ReactContextBaseJavaModule(context) {

    override fun getName(): String = "NativeCallback"

    private val activityLifecycleCallbacks = object : LifecycleEventListener {
        override fun onHostResume() {
            Log.d(TAG, "onHostResume: ")
            sendEvent("onHostResume",Arguments.createMap())
        }

        override fun onHostPause() {
            Log.d(TAG, "onHostPause: ")
            sendEvent("onHostPause",Arguments.createMap())
        }

        override fun onHostDestroy() {
            Log.d(TAG, "onHostDestroy: ")
            sendEvent("onHostDestroy",Arguments.createMap())
        }

    }

    init {
        reactApplicationContext.addLifecycleEventListener(activityLifecycleCallbacks)
    }

    fun sendEvent(eventName: String, params: WritableMap){
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params);
    }

    @ReactMethod
    fun test() {
        sendEvent("bingo",Arguments.createMap())
    }

    companion object {
        private const val TAG = "CallbackMethodModule"
    }
}