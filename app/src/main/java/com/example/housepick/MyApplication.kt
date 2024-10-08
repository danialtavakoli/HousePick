package com.example.housepick

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.housepick.data.utils.TokenUtils
import com.example.housepick.ui.utils.LocaleUtils

class MyApplication : Application() {
    companion object {
        var IP = "192.168.1.102:8080"
        var IPSocket = IP.split(":")[0]
        var JWT: String? = null
        var appContext: Context? = null
        var allowNotifications = false

        //true = map and false = list
        var homeListOrMap: Boolean = false
        private var idNotifs: Int = 1000

        fun getIdNotifParis(): Int {
            idNotifs += 1
            return idNotifs
        }

        private var ID: Int? = null


        fun getID(): Int? {
            if (ID === null) {
                ID = TokenUtils.getId()
            }
            return ID
        }

        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }

        private var activityVisible = false

        private lateinit var instance: MyApplication

        fun getInstance(): MyApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val sharedPreferences: SharedPreferences =
            applicationContext.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        allowNotifications = sharedPreferences.getBoolean("allowNotifs", true)
        appContext = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // Set default language
        LocaleUtils.initialize(applicationContext, LocaleUtils.getSelectedLanguageId())
        createNotificationChannel()
    }

    fun initAppLanguage(context: Context) {
        LocaleUtils.initialize(context, LocaleUtils.getSelectedLanguageId())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Notifications", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}