package uk.ac.tees.mad.quotepro.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uk.ac.tees.mad.quotepro.domain.model.ReminderStatus
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.GetReminderByIdUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.UpdateReminderUseCase
import uk.ac.tees.mad.quotepro.utils.NotificationHelper

@HiltWorker
class ReminderNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getReminderByIdUseCase: GetReminderByIdUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val REMINDER_ID = "reminder_id"
    }

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString(REMINDER_ID) ?: return Result.failure()

        return try {
            val reminder = getReminderByIdUseCase(reminderId)

            if (reminder != null && !reminder.isNotified) {
                // 1. Show Notification
                val notification = notificationHelper.buildReminderNotification(reminder)
                notificationHelper.showNotification(reminder.id.hashCode(), notification)

                // 2. Update Status in DB
                val updatedReminder = reminder.copy(
                    isNotified = true,
                    status = ReminderStatus.SENT
                )
                updateReminderUseCase(updatedReminder)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}