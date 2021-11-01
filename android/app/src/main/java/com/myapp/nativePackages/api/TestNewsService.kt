package com.myapp.nativePackages.api

import okhttp3.Response
import okhttp3.ResponseBody

class TestNewsService: NewsService {
    override fun getTopHeadlines(): Response {
        Response.Builder().body()
    }
}