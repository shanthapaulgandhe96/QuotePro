package uk.ac.tees.mad.quotepro.presentation.screens.reminder

import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.Reminder
import uk.ac.tees.mad.quotepro.domain.model.ReminderType

data class ReminderState(
    val reminders: List<Reminder> = emptyList(),
    val filteredReminders: List<Reminder> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    // Dialog States
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showQuoteSelectionDialog: Boolean = false, // New: To show list of quotes

    // Data for flows
    val selectedReminder: Reminder? = null,
    val availableQuotes: List<Quote> = emptyList(), // New: List to pick from
    val quoteIdForReminder: String = "",
    val clientNameForReminder: String = "",
    val quoteDueDateForReminder: Long? = null
)

sealed class ReminderUiEvent {
    // ... existing events ...
    data class SearchQueryChanged(val query: String) : ReminderUiEvent()
    data class ReminderClicked(val reminder: Reminder) : ReminderUiEvent()
    object AddReminderClicked : ReminderUiEvent() // Triggers loading quotes
    object DismissDialogs : ReminderUiEvent()

    // New: User selected a quote from the list
    data class QuoteSelectedForReminder(val quote: Quote) : ReminderUiEvent()

    // ... existing create/update events ...
    data class CreateReminder(
        val quoteId: String,
        val clientName: String,
        val dueDate: Long?,
        val type: ReminderType,
        val customDate: Long?
    ) : ReminderUiEvent()

    data class DeleteReminderClicked(val reminder: Reminder) : ReminderUiEvent()
    object ConfirmDelete : ReminderUiEvent()
    object Refresh : ReminderUiEvent()
    data class UpdateReminder(val reminder: Reminder) : ReminderUiEvent()
    data class EditReminderClicked(val reminder: Reminder) : ReminderUiEvent()
    data class AddReminderForQuote(val quoteId: String) : ReminderUiEvent()
}