package com.example.taskerclone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.taskerclone.data.ActionExecutor
import com.example.taskerclone.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Persistent foreground service. Keeps the app "alive" in the background
 * and polls TIME based profiles every 60 seconds.
 * WiFi/Battery/Boot triggers are handled separately via BroadcastReceivers.
 */
class AutomationService : Service() {

    private val CHANNEL_ID = "automation_service_channel"
    private val NOTIFICATION_ID = 1
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.IO)

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkTimeTriggers()
            handler.postDelayed(this, 60_000) // check every 60 sec
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        handler.post(checkRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // restart service if killed by system
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkRunnable)
    }

    private fun checkTimeTriggers() {
        scope.launch {
            val dao = AppDatabase.getInstance(applicationContext).profileDao()
            val profiles = dao.getEnabledByTriggerType("TIME")
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            for (profile in profiles) {
                if (profile.triggerValue == currentTime) {
                    ActionExecutor.execute(applicationContext, profile.actionType, profile.actionValue)
                }
            }
        }
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID, "Automation Service", NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TaskerClone running")
            .setContentText("Monitoring your automation profiles")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setOngoing(true)
            .build()
    }
}
