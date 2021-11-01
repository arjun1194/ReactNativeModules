package com.myapp.nativePackages.callback

import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CallbackModule(reactContext: ReactApplicationContext?): ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "CallbackModule"

    @ReactMethod
    fun sleep(seconds: Int,callback: Callback){
        CoroutineScope(Dispatchers.IO).launch {
            delay((seconds * 1000).toLong())
            callback.invoke()
        }
    }
}