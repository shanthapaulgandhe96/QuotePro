package uk.ac.tees.mad.quotepro.domain.usecase.reminder

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.repo.ReminderRepository
import javax.inject.Inject

class GetAllRemindersUseCase @Inject constructor(private val repository: ReminderRepository) {
    operator fun invoke(userId: String): Flow<List<Reminder>> = repository.getAllReminders(userId)
}

class GetRemindersForQuoteUseCase @Inject constructor(private val repository: ReminderRepository) {
    operator fun invoke(quoteId: String): Flow<List<Reminder>> = repository.getRemindersForQuote(quoteId)
}

class GetUpcomingRemindersUseCase @Inject constructor(private val repository: ReminderRepository) {
    operator fun invoke(userId: String): Flow<List<Reminder>> = repository.getUpcomingReminders(userId)
}