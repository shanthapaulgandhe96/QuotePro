package uk.ac.tees.mad.quotepro.presentation.screens.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.Client
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetAllQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getAllRemindersUseCase: GetAllRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val scheduleUseCase: ScheduleReminderNotificationUseCase,
    private val syncRemindersUseCase: SyncRemindersUseCase,
    private val getAllQuotesUseCase: GetAllQuotesUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ReminderState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ReminderUiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllRemindersUseCase(userId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { list ->
                    _state.update {
                        it.copy(
                            reminders = list,
                            filteredReminders = filterList(list, it.searchQuery),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadQuotesForSelection() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // FIX: Map QuoteEntity to Quote domain model to resolve type mismatch
            getAllQuotesUseCase(userId)
                .map { entities ->
                    entities.map { it.toDomain() }
                }
                .catch { e ->
                    _effect.emit(ReminderUiEffect.ShowToast("Failed to load quotes: ${e.message}"))
                }
                .collect { quotes ->
                    _state.update {
                        it.copy(
                            availableQuotes = quotes,
                            showQuoteSelectionDialog = true
                        )
                    }
                }
        }
    }

    fun onEvent(event: ReminderUiEvent) {
        when (event) {
            is ReminderUiEvent.SearchQueryChanged -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        filteredReminders = filterList(it.reminders, event.query)
                    )
                }
            }
            is ReminderUiEvent.AddReminderClicked -> {
                loadQuotesForSelection()
            }
            is ReminderUiEvent.QuoteSelectedForReminder -> {
                _state.update {
                    it.copy(
                        showQuoteSelectionDialog = false,
                        showAddDialog = true,
                        quoteIdForReminder = event.quote.id,
                        clientNameForReminder = event.quote.client.name,
                        quoteDueDateForReminder = event.quote.dueDate
                    )
                }
            }
            is ReminderUiEvent.CreateReminder -> createReminder(event)
            is ReminderUiEvent.UpdateReminder -> updateReminder(event.reminder)
            is ReminderUiEvent.DeleteReminderClicked -> {
                _state.update { it.copy(showDeleteDialog = true, selectedReminder = event.reminder) }
            }
            is ReminderUiEvent.ConfirmDelete -> deleteReminder()
            is ReminderUiEvent.DismissDialogs -> {
                _state.update {
                    it.copy(
                        showAddDialog = false,
                        showEditDialog = false,
                        showDeleteDialog = false,
                        showQuoteSelectionDialog = false,
                        selectedReminder = null
                    )
                }
            }
            is ReminderUiEvent.ReminderClicked -> {
                viewModelScope.launch {
                    _effect.emit(ReminderUiEffect.NavigateToQuote(event.reminder.quoteId))
                }
            }
            is ReminderUiEvent.Refresh -> {
                val userId = auth.currentUser?.uid ?: return
                viewModelScope.launch {
                    syncRemindersUseCase(userId)
                    _effect.emit(ReminderUiEffect.ShowToast("Syncing reminders..."))
                }
            }
            else -> {}
        }
    }

    private fun filterList(list: List<uk.ac.tees.mad.quotepro.domain.model.Reminder>, query: String): List<uk.ac.tees.mad.quotepro.domain.model.Reminder> {
        if (query.isBlank()) return list
        return list.filter { it.clientName.contains(query, ignoreCase = true) }
    }

    private fun createReminder(event: ReminderUiEvent.CreateReminder) {
        viewModelScope.launch {
            createReminderUseCase(
                quoteId = event.quoteId,
                clientName = event.clientName,
                dueDate = event.dueDate,
                type = event.type,
                customDate = event.customDate
            ).onSuccess { reminder ->
                scheduleUseCase(reminder)
                _effect.emit(ReminderUiEffect.ShowToast("Reminder set successfully"))
                _state.update { it.copy(showAddDialog = false) }
            }.onFailure {
                _effect.emit(ReminderUiEffect.ShowToast("Error: ${it.message}"))
            }
        }
    }

    private fun updateReminder(reminder: uk.ac.tees.mad.quotepro.domain.model.Reminder) {
        viewModelScope.launch {
            updateReminderUseCase(reminder).onSuccess {
                scheduleUseCase(reminder)
                _effect.emit(ReminderUiEffect.ShowToast("Reminder updated"))
                _state.update { it.copy(showEditDialog = false) }
            }.onFailure {
                _effect.emit(ReminderUiEffect.ShowToast("Update failed"))
            }
        }
    }

    private fun deleteReminder() {
        val reminder = _state.value.selectedReminder ?: return
        viewModelScope.launch {
            deleteReminderUseCase(reminder.id).onSuccess {
                scheduleUseCase.cancel(reminder.id)
                _effect.emit(ReminderUiEffect.ShowToast("Reminder deleted"))
                _state.update { it.copy(showDeleteDialog = false, selectedReminder = null) }
            }
        }
    }
}

/**
 * Mapper function to convert Database Entity to Domain Model.
 * Updated to match the provided Quote domain model fields.
 */

fun QuoteEntity.toDomain(): Quote {
    return Quote(
        id = this.id,
        quoteNumber = this.quoteNumber,
        userId = this.userId,
        services = emptyList(), // Entities usually store JSON strings for lists, map if needed
        totalAmount = this.totalAmount,
        currency = this.currency,
        createdAt = this.createdAt,
        dueDate = this.dueDate
    )
}