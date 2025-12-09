package uk.ac.tees.mad.quotepro.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val storageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    /**
     * Returns the current date formatted for display (e.g., "12 Dec 2024").
     */
    fun getCurrentDateFormatted(): String {
        return displayDateFormat.format(Date())
    }

    /**
     * Formats a timestamp (Long) into a readable date string.
     */
    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    /**
     * Formats a timestamp (Long) into a readable date and time string.
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    /**
     * Formats a Date object to a string suitable for storage/sorting (e.g., "2024-12-12").
     */
    fun formatForStorage(date: Date): String {
        return storageDateFormat.format(date)
    }
}