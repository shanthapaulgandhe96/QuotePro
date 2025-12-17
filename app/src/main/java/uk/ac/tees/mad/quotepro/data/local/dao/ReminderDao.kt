package uk.ac.tees.mad.quotepro.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.entity.ReminderEntity

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: String)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY reminderDate ASC")
    fun getAllReminders(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE quoteId = :quoteId ORDER BY reminderDate ASC")
    fun getRemindersForQuote(quoteId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND reminderDate >= :currentTime ORDER BY reminderDate ASC")
    fun getUpcomingReminders(userId: String, currentTime: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedReminders(userId: String): List<ReminderEntity>
}