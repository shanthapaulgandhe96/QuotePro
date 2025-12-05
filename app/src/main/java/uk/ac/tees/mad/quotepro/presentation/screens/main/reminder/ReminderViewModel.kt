package uk.ac.tees.mad.quotepro.presentation.screens.main.reminder

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor() : ViewModel() {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private fun generateReminder(daysAhead: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAhead)
        return dateFormatter.format(calendar.time)
    }

    private val allReminders = listOf(
        ReminderUiModel("1", "John Doe", generateReminder(3)),
        ReminderUiModel("2", "Emma Smith", generateReminder(7)),
        ReminderUiModel("3", "David Miller", generateReminder(14)),
    )

    val reminders = mutableStateListOf<ReminderUiModel>().apply {
        addAll(allReminders)
    }

    fun onSearch(query: String) {
        reminders.clear()
        if (query.isBlank()) {
            reminders.addAll(allReminders)
        } else {
            reminders.addAll(
                allReminders.filter {
                    it.clientName.contains(query, ignoreCase = true)
                }
            )
        }
    }
}
