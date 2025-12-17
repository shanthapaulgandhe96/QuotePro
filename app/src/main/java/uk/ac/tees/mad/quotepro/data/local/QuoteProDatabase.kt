package uk.ac.tees.mad.quotepro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import uk.ac.tees.mad.quotepro.data.local.dao.QuoteDao
import uk.ac.tees.mad.quotepro.data.local.dao.ReminderDao // Added
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.data.local.entity.ReminderEntity // Added
import uk.ac.tees.mad.quotepro.utils.Constants

@Database(
    entities = [QuoteEntity::class, ReminderEntity::class], // Added ReminderEntity
    version = 2, // Incremented version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuoteProDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun reminderDao(): ReminderDao // Added

    companion object {
        // Migration strategy from V1 to V2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new reminders table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `reminders` (
                        `id` TEXT NOT NULL, 
                        `quoteId` TEXT NOT NULL, 
                        `userId` TEXT NOT NULL, 
                        `clientName` TEXT NOT NULL, 
                        `dueDate` INTEGER, 
                        `reminderDate` INTEGER NOT NULL, 
                        `isNotified` INTEGER NOT NULL, 
                        `isSynced` INTEGER NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `createdAt` INTEGER NOT NULL, 
                        `updatedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`quoteId`) REFERENCES `quotes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)
                // Create indices for faster lookup
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_reminders_quoteId` ON `reminders` (`quoteId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_reminders_reminderDate` ON `reminders` (`reminderDate`)")
            }
        }
    }
}