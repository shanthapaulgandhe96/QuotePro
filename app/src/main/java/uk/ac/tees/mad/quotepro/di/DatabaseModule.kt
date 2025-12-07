package uk.ac.tees.mad.quotepro.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.data.local.QuoteProDatabase
import uk.ac.tees.mad.quotepro.data.local.dao.QuoteDao
import uk.ac.tees.mad.quotepro.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuoteProDatabase(
        @ApplicationContext context: Context
    ): QuoteProDatabase {
        return Room.databaseBuilder(
            context,
            QuoteProDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQuoteDao(database: QuoteProDatabase): QuoteDao {
        return database.quoteDao()
    }
}