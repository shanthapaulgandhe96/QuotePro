package uk.ac.tees.mad.quotepro.utils

import uk.ac.tees.mad.quotepro.data.local.QuoteProDatabase
import javax.inject.Inject

class CacheManager @Inject constructor(
    private val database: QuoteProDatabase
) {
    suspend fun clearAllCache() {
        database.clearAllTables()
    }
}