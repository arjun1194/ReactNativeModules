package com.myapp.utils.workers

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkRequest

class WorkProgressManager(
    private val workRequests: List<WorkRequest>
)  {

    companion object {
        private const val TAG = "WorkProgressManager"
    }

    private var completedWorks = 0
    private var progressLiveData =  MutableLiveData<Int>()
    private val numberOrWorks = workRequests.size

    private fun getQuery(): WorkQuery {
        return WorkQuery.Builder
            .fromIds( workRequests.map { it.id } )
            .addStates(listOf(WorkInfo.State.RUNNING))
            .build()
    }

    /**
     * Initialize Work Progress Manager.
     * @return [LiveData] that contains the combined progress of all [workRequests]
     * for e.g. if we have 4 sequential tasks, A,B,C,D and currently A is at 100% completion.
     * [progressLiveData] will have the value = 25%
     */
    fun initialize(context: AppCompatActivity): LiveData<Int> {
        WorkManager.getInstance(context).getWorkInfosLiveData(getQuery()).observe(context) {
            if (it.size > 0) {
                 val currentWorkProgress = it[0].progress.getFloat(WorkerConstants.Progress,0f).toInt()
                Log.d(TAG, "initialize: $currentWorkProgress")
                val totalProgress = getTotalProgress(numberOrWorks,completedWorks,currentWorkProgress)
                incrementCompletedWorksIfNotLast(currentWorkProgress)
                progressLiveData.postValue(totalProgress)
            }
        }
        return progressLiveData
    }

    private fun incrementCompletedWorksIfNotLast(currentWorkProgress: Int) {
        if (currentWorkProgress == 100 && completedWorks!=numberOrWorks-1) completedWorks+=1
    }

    private fun getTotalProgress(
        numberOrWorks: Int,
        completedWorks: Int,
        currentWorkProgress: Int
    ): Int {
        return (100/numberOrWorks)* completedWorks + currentWorkProgress/numberOrWorks
    }
}