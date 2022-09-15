package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadManager:DownloadManager?=null;


    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    //Variable to store Url
    private lateinit var url: String

    //Variable to store title of Url
    private  lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        custom_button.setOnClickListener {
            startDownload()
        }
        createChannel(
            getString(R.string.channel_name)
        )
    }

    private fun createChannel(channelName:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

            //enable the lights when a notification is shown.
            notificationChannel.enableLights(true)

            //display a red light when a notification is shown.
            notificationChannel.lightColor = Color.RED

            //enable vibration.
            notificationChannel.enableVibration(true)

            //set channel description
            notificationChannel.description = getString(R.string.app_description)

            //Get instance of Notification Manager
            val  notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            if(id== downloadID){
                val cursor: Cursor? =
                    downloadManager?.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                notificationManager.sendNotification(
                                    applicationContext.getString(R.string.notification_description),
                                    applicationContext,
                                    CHANNEL_ID,
                                    title,
                                    true
                                )
                            }
                            DownloadManager.STATUS_FAILED -> {
                                notificationManager.sendNotification(
                                    applicationContext.getString(R.string.notification_description),
                                    applicationContext,
                                    CHANNEL_ID,
                                    title,
                                    false
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun startDownload(){
        when(download_radiogroup.checkedRadioButtonId){
            R.id.glide_radiobutton ->{
                url= GLIDE_URL
                title=  getString(R.string.download_using_glide)
                download()
            }
            R.id.loadapp_radiobutton ->{
                url= LOADAPP_URL
                title=  getString(R.string.download_using_loadapp)
                download()            }
            R.id.retrofit_radiobutton ->{
                url= RETROFIT_URL
                title=  getString(R.string.download_using_retrofit)
                download()
            }
            else -> Toast.makeText(this,getString(R.string.no_radio_button_clicked), Toast.LENGTH_LONG )

        }
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager?.enqueue(request)?:0// enqueue puts the download request in the queue.

        val notificationManager = ContextCompat.getSystemService(applicationContext,NotificationManager::class.java)
            as NotificationManager
        notificationManager.cancelNotifications()

    }

    companion object {
        private const val GLIDE_URL ="https://github.com/bumptech/glide"
        private const val LOADAPP_URL ="https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL="https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onResume() {
        super.onResume()
        super.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)

    }

}
