package uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes

import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus

// UI State
data class SavedQuotesState(
    val quotes: List<Quote> = emptyList(),
    val filteredQuotes: List<Quote> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: QuoteStatus? = null,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val showFilterSheet: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val quoteToDelete: Quote? = null
)

// UI Events
sealed class SavedQuotesEvent {
    data class SearchQueryChanged(val query: String) : SavedQuotesEvent()
    data class FilterByStatus(val status: QuoteStatus?) : SavedQuotesEvent()
    data class QuoteClicked(val quoteId: String) : SavedQuotesEvent()
    data class EditQuoteClicked(val quoteId: String) : SavedQuotesEvent()
    data class DeleteQuoteClicked(val quote: Quote) : SavedQuotesEvent()
    object ConfirmDelete : SavedQuotesEvent()
    object CancelDelete : SavedQuotesEvent()
    object ShowFilterSheet : SavedQuotesEvent()
    object HideFilterSheet : SavedQuotesEvent()
    object RefreshQuotes : SavedQuotesEvent()
}

// Navigation Actions
sealed class SavedQuotesNavAction {
    data class NavigateToQuoteDetail(val quoteId: String) : SavedQuotesNavAction()
    data class NavigateToEditQuote(val quoteId: String) : SavedQuotesNavAction()
}

// UI Effects
sealed class SavedQuotesEffect {
    data class ShowToast(val message: String) : SavedQuotesEffect()
    data class ShowError(val error: String) : SavedQuotesEffect()
}