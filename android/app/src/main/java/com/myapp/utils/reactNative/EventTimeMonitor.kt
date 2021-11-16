package com.myapp.utils.reactNative

import android.util.Log
import com.myapp.utils.io.TimeUtils

object EventTimeMonitor {
    private const val TAG = "EventTimeMonitor"
    /**
     * Holds a reference to System Milliseconds at which the last event was sent to JS.
     */
    private var lastEventSentTime: Long? = System.currentTimeMillis().also {
        Log.d(TAG, "init: $it")
    }

    /**
     * Total time in milliseconds we wait before sending out an Event Again to JS.
     */
    private var eventDelay = 1000

    /**
     * Checks if a minimum of [eventDelay] Milliseconds have elapsed before an event is sent to JS.
     */
    fun shouldSendEvent(): Boolean {
        val should = System.currentTimeMillis() - lastEventSentTime!! >= eventDelay
        if (should){
            Log.d(TAG, "sending event at ${TimeUtils.getTime()} ")
            lastEventSentTime = System.currentTimeMillis()
        }

        return should
    }
}