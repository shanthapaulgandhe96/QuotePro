package uk.ac.tees.mad.quotepro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uk.ac.tees.mad.quotepro.domain.model.ReminderStatus
import uk.ac.tees.mad.quotepro.domain.model.ReminderType

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE // If quote is deleted, delete reminders
        )
    ],
    indices = [Index(value = ["quoteId"]), Index(value = ["reminderDate"])]
)
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val quoteId: String,
    val userId: String, // Needed for querying all reminders for a specific user
    val clientName: String,
    val dueDate: Long?,
    val reminderDate: Long,
    val isNotified: Boolean,
    val isSynced: Boolean,
    val type: String, // Stored as String (Enum name)
    val status: String, // Stored as String (Enum name)
    val createdAt: Long,
    val updatedAt: Long
)