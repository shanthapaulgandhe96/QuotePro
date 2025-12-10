package uk.ac.tees.mad.quotepro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)

    @Query("DELETE FROM quotes WHERE id = :quoteId")
    suspend fun deleteQuoteById(quoteId: String)

    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    suspend fun getQuoteById(quoteId: String): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllQuotes(userId: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    fun getQuotesByStatus(userId: String, status: String): Flow<List<QuoteEntity>>

    @Query("""
        SELECT * FROM quotes 
        WHERE userId = :userId 
        AND (
            clientJson LIKE '%' || :searchQuery || '%' 
            OR quoteNumber LIKE '%' || :searchQuery || '%'
            OR servicesJson LIKE '%' || :searchQuery || '%'
        )
        ORDER BY createdAt DESC
    """)
    fun searchQuotes(userId: String, searchQuery: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedQuotes(userId: String): List<QuoteEntity>

    @Query("SELECT COUNT(*) FROM quotes WHERE userId = :userId")
    suspend fun getQuotesCount(userId: String): Int

    @Query("SELECT * FROM quotes WHERE userId = :userId AND dueDate < :currentTime AND status != 'PAID' ORDER BY dueDate ASC")
    fun getOverdueQuotes(userId: String, currentTime: Long): Flow<List<QuoteEntity>>
}