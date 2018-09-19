package org.dashj.dashjinterface.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.dashj.dashjinterface.R

class NotificationAgent(context: Context) {

    companion object {
        private const val SYNC_NOTIFICATION_CHANNEL_ID = "WalletAppKitServiceChannel"
        const val SYNC_NOTIFICATION_ID = 1
    }

    private val syncNotificationBuilder: NotificationCompat.Builder
    private var prevSyncProgress: Int = -1

    val syncNotification: Notification
        get() = syncNotificationBuilder.build()

    init {
        createNotificationChannel(context)
        syncNotificationBuilder = createSyncNotificationBuilder(
                context, context.getString(R.string.sync_notification_init_message))
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.sync_notification_channel_name)
            val description = context.getString(R.string.sync_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(SYNC_NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    private fun createSyncNotificationBuilder(context: Context, title: String?): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, SYNC_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_dash_d_24dp)
                .setProgress(0, 0, true)
                .setContentTitle(title)
                .setOnlyAlertOnce(true)
    }

    fun updateSyncProgress(context: Context, progressMax: Int, progress: Int) {
        val progressPercentage = (progress.toFloat() / progressMax.toFloat() * 100).toInt()
        if (prevSyncProgress != progressPercentage) {
            val contentTitle = context.getString(R.string.sync_notification_message)
            syncNotificationBuilder
                    .setContentTitle(contentTitle)
                    .setContentText("$progressPercentage%")
                    .setProgress(progressMax, progress, false)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(SYNC_NOTIFICATION_ID, syncNotificationBuilder.build())
        }
        prevSyncProgress = progressPercentage
    }
}
