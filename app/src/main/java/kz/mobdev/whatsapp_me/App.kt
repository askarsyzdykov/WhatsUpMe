package kz.mobdev.whatsapp_me

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initNotificationChannel()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun initNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Open WhatsApp",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}