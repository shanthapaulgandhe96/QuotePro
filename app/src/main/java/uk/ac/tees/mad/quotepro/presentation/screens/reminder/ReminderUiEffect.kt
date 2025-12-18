package uk.ac.tees.mad.quotepro.presentation.screens.reminder

sealed class ReminderUiEffect {
    data class ShowToast(val message: String) : ReminderUiEffect()
    data class NavigateToQuote(val quoteId: String) : ReminderUiEffect()
}