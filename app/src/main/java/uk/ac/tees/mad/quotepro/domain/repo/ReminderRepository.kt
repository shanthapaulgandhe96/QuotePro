package uk.ac.tees.mad.quotepro.domain.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.Reminder

interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Unit>
    suspend fun updateReminder(reminder: Reminder): Result<Unit>
    suspend fun deleteReminder(reminderId: String): Result<Unit>
    suspend fun getReminderById(id: String): Reminder?

    fun getAllReminders(userId: String): Flow<List<Reminder>>
    fun getRemindersForQuote(quoteId: String): Flow<List<Reminder>>
    fun getUpcomingReminders(userId: String): Flow<List<Reminder>>

    suspend fun syncUnsyncedReminders(userId: String): Result<Unit>
}