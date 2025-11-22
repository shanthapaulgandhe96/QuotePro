package uk.ac.tees.mad.quotepro.utils

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message.ifEmpty { "Unknown Message" }, Toast.LENGTH_SHORT).show()
}