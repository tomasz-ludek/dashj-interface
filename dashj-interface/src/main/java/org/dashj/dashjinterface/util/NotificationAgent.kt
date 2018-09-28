package org.dashj.dashjinterface.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import org.bitcoinj.core.MasternodeSync
import org.dashj.dashjinterface.R
import java.text.SimpleDateFormat
import java.util.*


class NotificationAgent(context: Context) {

    companion object {
        private const val SYNC_NOTIFICATION_CHANNEL_ID = "WalletAppKitServiceChannel"
        private const val NOTIFICATION_CLICK_RECEIVER_ACTION = "dashj.interface.action.NOTIFICATION_CLICK"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        const val SYNC_NOTIFICATION_ID = 1
    }

    private val syncNotificationBuilder: NotificationCompat.Builder
    private var prevSyncProgress: Int = -1

    val syncNotification: Notification
        get() = syncNotificationBuilder.build()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        syncNotificationBuilder = createSyncNotificationBuilder(
                context, context.getString(R.string.sync_notification_init_message))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.sync_notification_channel_name)
        val description = context.getString(R.string.sync_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(SYNC_NOTIFICATION_CHANNEL_ID, name, importance)
        channel.description = description
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)
    }

    private fun createSyncNotificationBuilder(context: Context, title: String?): NotificationCompat.Builder {

        val notificationBuilder = NotificationCompat.Builder(context, SYNC_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_dash_d_24dp)
                .setProgress(0, 0, true)
                .setContentTitle(title)
                .setOnlyAlertOnce(true)

        val broadcastIntent = Intent(NOTIFICATION_CLICK_RECEIVER_ACTION)
        val matchingReceivers = context.packageManager.queryBroadcastReceivers(broadcastIntent, 0)
        if (matchingReceivers != null && matchingReceivers.size > 0) {
            val activityInfo = matchingReceivers[0].activityInfo
            broadcastIntent.component = ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
            val contentIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            notificationBuilder.setContentIntent(contentIntent)
        }
        return notificationBuilder
    }

    fun updateBlockchainSyncProgress(context: Context, progressMax: Int, progress: Int) {
        val progressPercentage = (progress.toFloat() / progressMax.toFloat() * 100).toInt()
        if (prevSyncProgress != progressPercentage || progressMax == progress) {
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

    fun updateMasternodeSyncProgress(context: Context, newStatus: Int) {
        if (newStatus == MasternodeSync.MASTERNODE_SYNC_FINISHED) {
            val contentTitle = context.getString(R.string.sync_complete_notification_message)
            syncNotificationBuilder
                    .setProgress(0, 0, false)
                    .setContentTitle(contentTitle)
                    .setContentText(DATE_FORMAT.format(Date()))
        } else {
            val contentText = when (newStatus) {
                MasternodeSync.MASTERNODE_SYNC_FAILED -> "FAILED"
                MasternodeSync.MASTERNODE_SYNC_INITIAL -> "INITIAL"
                MasternodeSync.MASTERNODE_SYNC_WAITING -> "WAITING"
                MasternodeSync.MASTERNODE_SYNC_LIST -> "LIST"
                MasternodeSync.MASTERNODE_SYNC_MNW -> "MNW"
                MasternodeSync.MASTERNODE_SYNC_GOVERNANCE -> "GOVERNANCE"
                MasternodeSync.MASTERNODE_SYNC_GOVOBJ -> "GOVOBJ"
                MasternodeSync.MASTERNODE_SYNC_GOVOBJ_VOTE -> "GOVOBJ_VOTE"
                MasternodeSync.MASTERNODE_SYNC_FINISHED -> "FINISHED"
                else -> throw IllegalArgumentException("Unsupported sync status $newStatus")
            }
            val contentTitle = context.getString(R.string.sync_masternode_notification_message)
            syncNotificationBuilder
                    .setProgress(0, 0, true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(SYNC_NOTIFICATION_ID, syncNotificationBuilder.build())
    }
}
