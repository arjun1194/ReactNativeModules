package com.myapp.nativePackages.videoCompression

import android.net.Uri

interface VideoCompressor {
    /**
     * Compress a Video file
     * @param uri takes a Uri to a video file
     * @return Uri to a compressed Video file
     */
    suspend fun compressVideo(uri: Uri): Uri
}