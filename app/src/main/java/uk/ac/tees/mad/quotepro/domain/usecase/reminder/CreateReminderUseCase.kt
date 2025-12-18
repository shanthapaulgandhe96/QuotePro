package uk.ac.tees.mad.quotepro.domain.usecase.reminder

import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.model.ReminderType
import uk.ac.tees.mad.quotepro.domain.repo.ReminderRepository
import java.util.UUID
import javax.inject.Inject

class CreateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(
        quoteId: String,
        clientName: String,
        dueDate: Long?,
        type: ReminderType,
        customDate: Long? = null
    ): Result<Reminder> = runCatching {

        // Calculate reminder date based on type
        val reminderDate = when (type) {
            ReminderType.BEFORE_DUE -> (dueDate ?: System.currentTimeMillis()) - (3 * 24 * 60 * 60 * 1000) // 3 days before
            ReminderType.ON_DUE -> dueDate ?: System.currentTimeMillis()
            ReminderType.OVERDUE -> (dueDate ?: System.currentTimeMillis()) + (1 * 24 * 60 * 60 * 1000) // 1 day after
        }

        // Use custom date if provided (overrides automatic calculation)
        val finalDate = customDate ?: reminderDate

        val reminder = Reminder(
            id = UUID.randomUUID().toString(),
            quoteId = quoteId,
            clientName = clientName,
            dueDate = dueDate,
            reminderDate = finalDate,
            type = type
        )

        repository.createReminder(reminder).getOrThrow()

        return@runCatching reminder
    }
}