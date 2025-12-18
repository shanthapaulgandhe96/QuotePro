package uk.ac.tees.mad.quotepro.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.ac.tees.mad.quotepro.MainActivity
import uk.ac.tees.mad.quotepro.R
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "quote_reminders_channel"
        const val CHANNEL_NAME = "Invoice Reminders"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming and overdue invoices"
                enableLights(true)
                enableVibration(true)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun buildReminderNotification(reminder: Reminder): Notification {
        // Intent to open the Quote Detail screen via Deep Link
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "android-app://androidx.navigation/quoteDetail/${reminder.quoteId}".toUri(),
            context,
            MainActivity::class.java
        )

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                reminder.id.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } ?: throw IllegalStateException("PendingIntent is null")

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Ensure this resource exists
            .setContentTitle("Invoice Reminder: ${reminder.clientName}")
            .setContentText("Don't forget about the invoice for ${reminder.clientName}.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    fun showNotification(id: Int, notification: Notification) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }
}