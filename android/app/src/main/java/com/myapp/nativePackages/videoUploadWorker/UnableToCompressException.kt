package com.myapp.nativePackages.videoUploadWorker

import androidx.annotation.Keep

@Keep
class UnableToCompressException(message: String) : Exception(message)
