package uk.ac.tees.mad.quotepro.presentation.screens.quotedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.data.mapper.QuoteMapper
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import uk.ac.tees.mad.quotepro.domain.model.ReminderType
import uk.ac.tees.mad.quotepro.domain.usecase.quote.DeleteQuoteUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetQuoteByIdUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.UpdateQuoteStatusUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.CreateReminderUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.ScheduleReminderNotificationUseCase
import javax.inject.Inject

@HiltViewModel
class QuoteDetailViewModel @Inject constructor(
    private val getQuoteByIdUseCase: GetQuoteByIdUseCase,
    private val updateQuoteStatusUseCase: UpdateQuoteStatusUseCase,
    private val deleteQuoteUseCase: DeleteQuoteUseCase,
    private val createReminderUseCase: CreateReminderUseCase, // Injected
    private val scheduleReminderUseCase: ScheduleReminderNotificationUseCase // Injected
) : ViewModel() {

    private val _state = MutableStateFlow(QuoteDetailState())
    val state: StateFlow<QuoteDetailState> = _state.asStateFlow()

    private val _navAction = MutableSharedFlow<QuoteDetailNavAction>()
    val navAction = _navAction.asSharedFlow()

    private val _effect = MutableSharedFlow<QuoteDetailEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: QuoteDetailEvent) {
        when (event) {
            is QuoteDetailEvent.LoadQuote -> loadQuote(event.quoteId)
            is QuoteDetailEvent.UpdateStatus -> updateStatus(event.newStatus)
            is QuoteDetailEvent.EditQuote -> editQuote()
            is QuoteDetailEvent.DeleteQuote -> showDeleteDialog()
            is QuoteDetailEvent.ConfirmDelete -> confirmDelete()
            is QuoteDetailEvent.CancelDelete -> hideDeleteDialog()

            // New Events
            is QuoteDetailEvent.OpenReminderDialog -> _state.update { it.copy(showReminderDialog = true) }
            is QuoteDetailEvent.DismissReminderDialog -> _state.update { it.copy(showReminderDialog = false) }
            is QuoteDetailEvent.SetReminder -> createReminder(event.type, event.customDate)
        }
    }

    private fun loadQuote(quoteId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getQuoteByIdUseCase(quoteId)
                .onSuccess { quoteEntity ->
                    if (quoteEntity != null) {
                        val quote = QuoteMapper.toDomain(quoteEntity)
                        _state.update {
                            it.copy(
                                quote = quote,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Quote not found"
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load quote"
                        )
                    }
                    _effect.emit(QuoteDetailEffect.ShowError(exception.message ?: "Failed to load quote"))
                }
        }
    }

    private fun updateStatus(newStatus: QuoteStatus) {
        viewModelScope.launch {
            val currentQuote = _state.value.quote ?: return@launch
            val updatedQuote = currentQuote.copy(
                status = newStatus,
                updatedAt = System.currentTimeMillis()
            )
            updateQuoteStatusUseCase(updatedQuote)
                .onSuccess {
                    _state.update { it.copy(quote = updatedQuote) }
                    _effect.emit(QuoteDetailEffect.ShowToast("Status updated to ${newStatus.name}"))
                }
                .onFailure { exception ->
                    _effect.emit(QuoteDetailEffect.ShowError(exception.message ?: "Failed to update status"))
                }
        }
    }

    private fun createReminder(type: ReminderType, customDate: Long?) {
        val quote = _state.value.quote ?: return
        viewModelScope.launch {
            createReminderUseCase(
                quoteId = quote.id,
                clientName = quote.client.name,
                dueDate = quote.dueDate,
                type = type,
                customDate = customDate
            ).onSuccess { reminder ->
                // Schedule local notification immediately
                scheduleReminderUseCase(reminder)

                _state.update { it.copy(showReminderDialog = false) }
                _effect.emit(QuoteDetailEffect.ShowToast("Reminder set successfully"))
            }.onFailure { e ->
                _effect.emit(QuoteDetailEffect.ShowError(e.message ?: "Failed to set reminder"))
            }
        }
    }

    private fun editQuote() {
        viewModelScope.launch {
            val quoteId = _state.value.quote?.id ?: return@launch
            _navAction.emit(QuoteDetailNavAction.NavigateToEdit(quoteId))
        }
    }

    private fun showDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    private fun hideDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            val currentQuote = _state.value.quote ?: return@launch
            val quoteEntity = QuoteMapper.toEntity(currentQuote)

            deleteQuoteUseCase(quoteEntity)
                .onSuccess {
                    _effect.emit(QuoteDetailEffect.ShowToast("Quote deleted successfully"))
                    _navAction.emit(QuoteDetailNavAction.NavigateBack)
                }
                .onFailure { exception ->
                    _effect.emit(QuoteDetailEffect.ShowError(exception.message ?: "Failed to delete quote"))
                    _state.update { it.copy(showDeleteDialog = false) }
                }
        }
    }
}

// --- Contract Classes ---

data class QuoteDetailState(
    val quote: Quote? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val showReminderDialog: Boolean = false // Added
)

sealed class QuoteDetailEvent {
    data class LoadQuote(val quoteId: String) : QuoteDetailEvent()
    data class UpdateStatus(val newStatus: QuoteStatus) : QuoteDetailEvent()
    object EditQuote : QuoteDetailEvent()
    object DeleteQuote : QuoteDetailEvent()
    object ConfirmDelete : QuoteDetailEvent()
    object CancelDelete : QuoteDetailEvent()

    // New Events for Reminder
    object OpenReminderDialog : QuoteDetailEvent()
    object DismissReminderDialog : QuoteDetailEvent()
    data class SetReminder(val type: ReminderType, val customDate: Long?) : QuoteDetailEvent()
}

sealed class QuoteDetailNavAction {
    object NavigateBack : QuoteDetailNavAction()
    data class NavigateToEdit(val quoteId: String) : QuoteDetailNavAction()
}

sealed class QuoteDetailEffect {
    data class ShowToast(val message: String) : QuoteDetailEffect()
    data class ShowError(val error: String) : QuoteDetailEffect()
}