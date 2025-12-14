package uk.ac.tees.mad.quotepro.presentation.screens.saveQuotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.data.mapper.QuoteMapper
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import uk.ac.tees.mad.quotepro.domain.usecase.quote.DeleteQuoteUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.FilterQuotesByStatusUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetAllQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SearchQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SyncOfflineQuotesUseCase
import javax.inject.Inject

@HiltViewModel
class SavedQuotesViewModel @Inject constructor(
    private val getAllQuotesUseCase: GetAllQuotesUseCase,
    private val deleteQuoteUseCase: DeleteQuoteUseCase,
    private val searchQuotesUseCase: SearchQuotesUseCase,
    private val filterQuotesByStatusUseCase: FilterQuotesByStatusUseCase,
    private val syncOfflineQuotesUseCase: SyncOfflineQuotesUseCase,
    private val authRepo: FirebaseAuthRepo
) : ViewModel() {

    private val _state = MutableStateFlow(SavedQuotesState())
    val state: StateFlow<SavedQuotesState> = _state.asStateFlow()

    private val _navAction = MutableSharedFlow<SavedQuotesNavAction>()
    val navAction = _navAction.asSharedFlow()

    private val _effect = MutableSharedFlow<SavedQuotesEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadQuotes()
    }

    fun onEvent(event: SavedQuotesEvent) {
        when (event) {
            is SavedQuotesEvent.SearchQueryChanged -> handleSearch(event.query)
            is SavedQuotesEvent.FilterByStatus -> handleFilterByStatus(event.status)
            is SavedQuotesEvent.QuoteClicked -> navigateToQuoteDetail(event.quoteId)
            is SavedQuotesEvent.EditQuoteClicked -> navigateToEditQuote(event.quoteId)
            is SavedQuotesEvent.DeleteQuoteClicked -> showDeleteDialog(event.quote)
            is SavedQuotesEvent.ConfirmDelete -> confirmDelete()
            is SavedQuotesEvent.CancelDelete -> hideDeleteDialog()
            is SavedQuotesEvent.ShowFilterSheet -> showFilterSheet()
            is SavedQuotesEvent.HideFilterSheet -> hideFilterSheet()
            is SavedQuotesEvent.RefreshQuotes -> refreshQuotes()
        }
    }

    private fun loadQuotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authRepo.getCurrentUser()?.uid ?: run {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "User not authenticated"
                    )
                }
                return@launch
            }

            getAllQuotesUseCase(userId).collect { quoteEntities ->
                val quotes = quoteEntities.map { QuoteMapper.toDomain(it) }
                _state.update {
                    it.copy(
                        quotes = quotes,
                        filteredQuotes = applyFilters(quotes, it.searchQuery, it.selectedStatus),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                filteredQuotes = applyFilters(it.quotes, query, it.selectedStatus)
            )
        }
    }

    private fun handleFilterByStatus(status: QuoteStatus?) {
        _state.update {
            it.copy(
                selectedStatus = status,
                filteredQuotes = applyFilters(it.quotes, it.searchQuery, status)
            )
        }
    }

    private fun applyFilters(
        quotes: List<Quote>,
        searchQuery: String,
        status: QuoteStatus?
    ): List<Quote> {
        var filtered = quotes

        // Apply status filter
        filtered = filterQuotesByStatusUseCase(filtered, status)

        // Apply search filter
        filtered = searchQuotesUseCase(filtered, searchQuery)

        return filtered
    }

    private fun navigateToQuoteDetail(quoteId: String) {
        viewModelScope.launch {
            _navAction.emit(SavedQuotesNavAction.NavigateToQuoteDetail(quoteId))
        }
    }

    private fun navigateToEditQuote(quoteId: String) {
        viewModelScope.launch {
            _navAction.emit(SavedQuotesNavAction.NavigateToEditQuote(quoteId))
        }
    }

    private fun showDeleteDialog(quote: Quote) {
        _state.update {
            it.copy(
                showDeleteDialog = true,
                quoteToDelete = quote
            )
        }
    }

    private fun hideDeleteDialog() {
        _state.update {
            it.copy(
                showDeleteDialog = false,
                quoteToDelete = null
            )
        }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            val quoteToDelete = _state.value.quoteToDelete ?: return@launch
            val quoteEntity = QuoteMapper.toEntity(quoteToDelete)

            deleteQuoteUseCase(quoteEntity)
                .onSuccess {
                    _effect.emit(SavedQuotesEffect.ShowToast("Quote deleted successfully"))
                    hideDeleteDialog()
                }
                .onFailure { exception ->
                    _effect.emit(SavedQuotesEffect.ShowError(exception.message ?: "Failed to delete quote"))
                    hideDeleteDialog()
                }
        }
    }

    private fun showFilterSheet() {
        _state.update { it.copy(showFilterSheet = true) }
    }

    private fun hideFilterSheet() {
        _state.update { it.copy(showFilterSheet = false) }
    }

    private fun refreshQuotes() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }

            // Sync offline quotes
            syncOfflineQuotesUseCase()
                .onSuccess {
                    _effect.emit(SavedQuotesEffect.ShowToast("Quotes synced successfully"))
                }
                .onFailure { exception ->
                    _effect.emit(SavedQuotesEffect.ShowError(exception.message ?: "Failed to sync quotes"))
                }

            _state.update { it.copy(isSyncing = false) }

            // Reload quotes
            loadQuotes()
        }
    }
}