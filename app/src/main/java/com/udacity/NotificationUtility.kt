package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    channelId: String,
    fileName: String,
    isSuccess: Boolean
) {

    val intent = Intent(applicationContext, DetailActivity::class.java)
    intent.putExtra("fileName", fileName)
    intent.putExtra("status", isSuccess)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        channelId
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            applicationContext.getString(R.string.button_name),
            pendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
