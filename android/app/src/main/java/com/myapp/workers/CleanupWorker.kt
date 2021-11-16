package com.myapp.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class CleanupWorker(context: Context,params: WorkerParameters): Worker(context,params) {
    override fun doWork(): Result {
        cleanupTempFiles()
        return Result.success()
    }

    private fun cleanupTempFiles() {

        val filesDir = File(applicationContext.filesDir.path)
        if(filesDir.isDirectory) {
            //TODO delete WorkConstants.TEMP_FILENAME
            filesDir.listFiles()?.forEach {
                if (it.exists()) {
                    it.deleteRecursively()
                }
            }
        }
    }
}