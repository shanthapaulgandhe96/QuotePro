package uk.ac.tees.mad.quotepro.domain.usecase.reminder

import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.repo.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> = repository.updateReminder(reminder)
}

class DeleteReminderUseCase @Inject constructor(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminderId: String): Result<Unit> = repository.deleteReminder(reminderId)
}

class SyncRemindersUseCase @Inject constructor(private val repository: ReminderRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.syncUnsyncedReminders(userId)
}

class GetReminderByIdUseCase @Inject constructor(private val repository: ReminderRepository) {
    suspend operator fun invoke(id: String): Reminder? = repository.getReminderById(id)
}