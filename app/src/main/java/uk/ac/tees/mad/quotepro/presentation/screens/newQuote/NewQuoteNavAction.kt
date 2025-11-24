package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

sealed class NewQuoteNavAction {
    object NavigateToSavedQuotes : NewQuoteNavAction()
    data class ShowSuccessMessage(val message: String) : NewQuoteNavAction()
}