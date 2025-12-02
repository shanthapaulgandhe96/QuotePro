package uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SavedQuotesViewModel @Inject constructor() : ViewModel() {

    private val _quotes = MutableStateFlow(
        listOf(
            QuoteUiModel("1", "John Doe", 1200.0, "2025-01-10"),
            QuoteUiModel("2", "Emily Smith", 950.0, "2025-01-08"),
            QuoteUiModel("3", "Michael Lee", 1500.0, "2025-01-05")
        )
    )
    val quotes: StateFlow<List<QuoteUiModel>> = _quotes

    fun onSearch(query: String) {
        _quotes.value = _quotes.value.filter {
            it.clientName.contains(query, ignoreCase = true)
        }
    }
}


// Dummy UI Model - replace after real DB integration
data class QuoteUiModel(
    val id: String,
    val clientName: String,
    val totalAmount: Double,
    val date: String
)
