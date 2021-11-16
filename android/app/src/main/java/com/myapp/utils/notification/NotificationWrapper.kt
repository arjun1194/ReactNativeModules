package com.myapp.utils.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.myapp.R

class NotificationWrapper(private val context: Context) {


    fun getNotification(notification: AppNotifications) : NotificationCompat.Builder{
        return when(notification){
            AppNotifications.CREATE_POST_PROGRESS_NOTIFICATION -> {
                NotificationCompat.Builder(context,getNotificationChannelId(notification))
                    .setSmallIcon(R.drawable.ic_download)
                    .setColor(ContextCompat.getColor(context,R.color.blue_500))
                    .setAutoCancel(false)
                    .setColorized(true)
                    .setSilent(true)
            }
        }
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(context,NotificationManager::class.java)!!
    }

    private fun getNotificationChannelId(notification: AppNotifications) : String {
        return when(notification) {
            AppNotifications.CREATE_POST_PROGRESS_NOTIFICATION -> context.getString(R.string.main_channel_id)
        }
    }
}

