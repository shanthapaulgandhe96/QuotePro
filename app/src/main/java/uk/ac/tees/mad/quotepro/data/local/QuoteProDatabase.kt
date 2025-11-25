package uk.ac.tees.mad.quotepro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.tees.mad.quotepro.data.local.dao.QuoteDao
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.utils.Constants

@Database(
    entities = [QuoteEntity::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuoteProDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}