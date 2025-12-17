package uk.ac.tees.mad.quotepro.data.mapper

import uk.ac.tees.mad.quotepro.data.local.entity.ReminderEntity
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.model.ReminderStatus
import uk.ac.tees.mad.quotepro.domain.model.ReminderType

fun Reminder.toEntity(userId: String): ReminderEntity {
    return ReminderEntity(
        id = id,
        quoteId = quoteId,
        userId = userId,
        clientName = clientName,
        dueDate = dueDate,
        reminderDate = reminderDate,
        isNotified = isNotified,
        isSynced = isSynced,
        type = type.name,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        quoteId = quoteId,
        clientName = clientName,
        dueDate = dueDate,
        reminderDate = reminderDate,
        isNotified = isNotified,
        isSynced = isSynced,
        type = ReminderType.valueOf(type),
        status = ReminderStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Reminder.toFirestoreMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "quoteId" to quoteId,
        "clientName" to clientName,
        "dueDate" to dueDate as Any,
        "reminderDate" to reminderDate,
        "isNotified" to isNotified,
        "type" to type.name,
        "status" to status.name,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

// Helper to update sync status locally
fun ReminderEntity.copyAsSynced(): ReminderEntity {
    return this.copy(isSynced = true)
}