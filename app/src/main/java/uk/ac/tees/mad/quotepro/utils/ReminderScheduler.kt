package uk.ac.tees.mad.quotepro.utils

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.workers.ReminderNotificationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {

    fun scheduleReminder(reminder: Reminder) {
        val delay = reminder.reminderDate - System.currentTimeMillis()

        if (delay <= 0) return // Don't schedule past reminders

        val data = workDataOf(ReminderNotificationWorker.REMINDER_ID to reminder.id)

        val workRequest = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag("reminder_${reminder.id}")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_${reminder.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(reminderId: String) {
        workManager.cancelUniqueWork("reminder_${reminderId}")
    }
}