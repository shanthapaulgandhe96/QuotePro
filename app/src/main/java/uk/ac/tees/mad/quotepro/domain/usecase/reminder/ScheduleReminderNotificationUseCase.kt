package uk.ac.tees.mad.quotepro.domain.usecase.reminder

import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.utils.ReminderScheduler
import javax.inject.Inject

class ScheduleReminderNotificationUseCase @Inject constructor(
    private val scheduler: ReminderScheduler
) {
    operator fun invoke(reminder: Reminder) {
        scheduler.scheduleReminder(reminder)
    }

    fun cancel(reminderId: String) {
        scheduler.cancelReminder(reminderId)
    }
}