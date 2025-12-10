package uk.ac.tees.mad.quotepro.domain.repo

interface SyncRepository {
    suspend fun syncOfflineQuotes()
    suspend fun isOnline(): Boolean
    suspend fun markQuoteAsSynced(quoteId: String)
}