package uk.ac.tees.mad.quotepro.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.data.local.dao.ReminderDao
import uk.ac.tees.mad.quotepro.data.mapper.copyAsSynced
import uk.ac.tees.mad.quotepro.data.mapper.toDomain
import uk.ac.tees.mad.quotepro.data.mapper.toEntity
import uk.ac.tees.mad.quotepro.data.mapper.toFirestoreMap
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.repo.ReminderRepository
import uk.ac.tees.mad.quotepro.utils.Constants
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ReminderRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    override suspend fun createReminder(reminder: Reminder): Result<Unit> = runCatching {
        if (currentUserId.isEmpty()) throw Exception("User not logged in")

        // 1. Save locally first (Offline First)
        reminderDao.insertReminder(reminder.toEntity(currentUserId))

        // 2. Try syncing to Firestore
        try {
            firestore.collection(Constants.REMINDERS_COLLECTION)
                .document(reminder.id)
                .set(reminder.toFirestoreMap() + ("userId" to currentUserId))
                .await()

            // Mark as synced locally
            reminderDao.insertReminder(reminder.toEntity(currentUserId).copyAsSynced())
        } catch (e: Exception) {
            // Keep local isSynced = false, SyncWorker will handle it later
            e.printStackTrace()
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit> = runCatching {
        if (currentUserId.isEmpty()) throw Exception("User not logged in")

        val updatedReminder = reminder.copy(updatedAt = System.currentTimeMillis())

        // Update local
        reminderDao.updateReminder(updatedReminder.toEntity(currentUserId))

        // Try remote update
        try {
            firestore.collection(Constants.REMINDERS_COLLECTION)
                .document(reminder.id)
                .update(updatedReminder.toFirestoreMap())
                .await()

            reminderDao.updateReminder(updatedReminder.toEntity(currentUserId).copyAsSynced())
        } catch (e: Exception) {
            // Failed to sync, remains dirty locally
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit> = runCatching {
        // Delete local
        reminderDao.deleteReminderById(reminderId)

        // Delete remote
        try {
            firestore.collection(Constants.REMINDERS_COLLECTION)
                .document(reminderId)
                .delete()
                .await()
        } catch (e: Exception) {
            // If offline, we might need a "deleted_reminders" table or logic
            // but for now, simple best-effort deletion is standard for MVP
        }
    }

    override suspend fun getReminderById(id: String): Reminder? {
        return reminderDao.getReminderById(id)?.toDomain()
    }

    override fun getAllReminders(userId: String): Flow<List<Reminder>> {
        return reminderDao.getAllReminders(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRemindersForQuote(quoteId: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersForQuote(quoteId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUpcomingReminders(userId: String): Flow<List<Reminder>> {
        return reminderDao.getUpcomingReminders(userId, System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncUnsyncedReminders(userId: String): Result<Unit> = runCatching {
        val unsynced = reminderDao.getUnsyncedReminders(userId)

        unsynced.forEach { entity ->
            try {
                firestore.collection(Constants.REMINDERS_COLLECTION)
                    .document(entity.id)
                    .set(entity.toDomain().toFirestoreMap() + ("userId" to userId))
                    .await()

                reminderDao.updateReminder(entity.copyAsSynced())
            } catch (e: Exception) {
                // Continue to next item
                e.printStackTrace()
            }
        }
    }
}