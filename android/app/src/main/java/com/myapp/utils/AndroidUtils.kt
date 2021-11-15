package com.myapp.utils

import android.util.Log


inline fun <reified T> T.logi(TAG: String, fieldName:String? = T::class.java.name): T {
    Log.i(TAG,"${fieldName?:T::class.java.simpleName} ---> $this")
    return this
}

inline fun <reified T> T.logd(TAG: String, fieldName:String? = T::class.java.name): T {
    Log.d(TAG,"$fieldName ---> $this")
    return this
}

inline fun <reified T: Throwable> T.loge(TAG: String, fieldName:String? = T::class.java.name) {
    Log.e(TAG,"$fieldName",this)
}
