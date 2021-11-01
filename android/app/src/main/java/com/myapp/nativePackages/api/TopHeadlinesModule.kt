package com.myapp.nativePackages.api

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class TopHeadlinesModule(reactContext: ReactApplicationContext?) :
    ReactContextBaseJavaModule(reactContext) {
    val newsService : NewsService by
    override fun getName(): String {
        return "TopHeadlines"
    }

    @ReactMethod
    fun get(url: String?, apiKey: String?, promise: Promise?) {
        val client = OkHttpClient().newBuilder().build()
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us")
            .method("GET", null)
            .addHeader("x-api-key", "416399bd74834bf0b8cc23cbc9985360")
            .build()
        val handler = CoroutineExceptionHandler { _,throwable ->
            promise?.reject(throwable)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            val response = client.newCall(request).execute()
            promise?.resolve(response.body?.string())

        }
    }
}