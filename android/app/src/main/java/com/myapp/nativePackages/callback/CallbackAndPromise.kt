package com.myapp.nativePackages.callback

import android.widget.Toast
import com.facebook.react.bridge.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CallbackAndPromise(reactContext: ReactApplicationContext?): ReactContextBaseJavaModule(reactContext) {


    override fun getName(): String {
        return "CallbackPromise"
    }


    @ReactMethod
    fun saySomething(name: String) {
        Toast.makeText(reactApplicationContext,name,Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    fun doWork(name: String, promise: Promise){
        CoroutineScope(Dispatchers.IO).launch {

            delay(3000)
            promise.resolve("Done!")
        }
    }

}