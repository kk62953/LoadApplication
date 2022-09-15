package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //Call notification manager and cancal all notifications
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()

        //Get the repository name and status of the download from MainActivity class
        val intent = getIntent()
        urlNameLabel.text = intent.getStringExtra("fileName")

        val isSuccess = intent.getBooleanExtra("status", false)
        if (isSuccess) {
            statusValueLabel.text = getString(R.string.success)
            statusValueLabel.setTextColor(getColor(R.color.colorPrimaryDark))
        } else {
            statusValueLabel.text = getString(R.string.fail)
            statusValueLabel.setTextColor(getColor(R.color.red))
        }


        okButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}
