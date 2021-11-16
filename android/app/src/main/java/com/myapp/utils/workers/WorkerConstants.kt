package com.myapp.utils.workers

object WorkerConstants {

    const val TEMP_FILENAME = "temp.mp4"

    // Progress key for WorkProgress Manager
    // all workers that update progress should use this key to post progress
    const val Progress = "progress"

    // Video Compress Worker keys
    const val VIDEO_COMPRESS_INPUT = "video_compress_input"

    // Video Upload Worker keys
    // when compression is done, we have file:// uri otherwise we have a content:// uri
    // TODO try to get temp file content uri
    const val IS_CONTENT = "is_content"
    const val VIDEO_UPLOAD_INPUT = "video_upload_input"

}