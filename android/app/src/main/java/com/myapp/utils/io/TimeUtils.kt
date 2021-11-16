package com.myapp.utils.io

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun getTime(): String {
        return dateFormat.format(Date())
    }
}