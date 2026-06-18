package com.business.fityou.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import com.business.fityou.MainActivity
import com.business.fityou.R
import com.business.fityou.util.getTimeStringFromDouble
import java.util.*

@AndroidEntryPoint
class WorkoutTimerService: Service() {



    override fun onBind(p0: Intent?): IBinder? = null

    private val timer: Timer = Timer()
    private var isTimerRunning = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timeElapsed = intent.getDoubleExtra(TIME_ELAPSED, 0.0)
        isTimerRunning = intent.getBooleanExtra(TIMER_RUNNING, false)

        startForegroundService(timeElapsed)

        timer.schedule(TimeTask(timeElapsed), 1000, 1000)
        return START_NOT_STICKY
    }

    private inner class TimeTask (private var timeElapsed: Double) : TimerTask() {
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            intent.setPackage(packageName)
            timeElapsed++
            updateNotification(timeElapsed)
            intent.putExtra(TIME_ELAPSED, timeElapsed)
            intent.putExtra(TIMER_RUNNING,isTimerRunning)
            sendBroadcast(intent)
        }

    }

    override fun stopService(name: Intent?): Boolean {
        isTimerRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        return super.stopService(name)
    }

    override fun onDestroy() {
        timer.cancel()
        isTimerRunning = false
        super.onDestroy()
    }

    private fun updateNotification(timeElapsed: Double) {
        val time = getTimeStringFromDouble(timeElapsed)
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("ongoing workout")
            .setContentText(time)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(getMainActivityPendingIntent())
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun startForegroundService(timeElapsed: Double) {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val time = getTimeStringFromDouble(timeElapsed)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("ongoing workout")
            .setContentText(time)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(getMainActivityPendingIntent())
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW,
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


    companion object {

        const val TIMER_UPDATED = "timerUpdated"
        const val TIMER_RUNNING = "isTimerRunning"
        const val TIME_ELAPSED = "timeElapsed"
        const val NOTIFICATION_CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_NAME = "notification"
    }
}
