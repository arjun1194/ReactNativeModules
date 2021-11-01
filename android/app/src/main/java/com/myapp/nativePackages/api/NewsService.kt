package com.myapp.nativePackages.api

import okhttp3.Response

interface NewsService {
    fun getTopHeadlines(): Response
}