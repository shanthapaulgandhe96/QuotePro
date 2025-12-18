package uk.ac.tees.mad.quotepro.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.data.repo.CloudinaryStorageRepositoryImpl
import uk.ac.tees.mad.quotepro.data.repo.CurrencyRepositoryImpl
import uk.ac.tees.mad.quotepro.data.repo.FirebaseAuthRepoImpl
import uk.ac.tees.mad.quotepro.data.repo.NewQuoteRepoImpl
import uk.ac.tees.mad.quotepro.data.repo.ProfileRepositoryImpl
import uk.ac.tees.mad.quotepro.data.repo.ReminderRepositoryImpl
import uk.ac.tees.mad.quotepro.data.repo.SyncRepositoryImpl
import uk.ac.tees.mad.quotepro.data.repo.UserPreferencesRepositoryImpl
import uk.ac.tees.mad.quotepro.domain.repo.CurrencyRepository
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import uk.ac.tees.mad.quotepro.domain.repo.ImageStorageRepository
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import uk.ac.tees.mad.quotepro.domain.repo.ProfileRepository
import uk.ac.tees.mad.quotepro.domain.repo.ReminderRepository
import uk.ac.tees.mad.quotepro.domain.repo.SyncRepository
import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepo(impl: FirebaseAuthRepoImpl): FirebaseAuthRepo

    @Binds
    @Singleton
    fun bindNewQuoteRepo(impl: NewQuoteRepoImpl): NewQuoteRepo

    @Binds
    @Singleton
    fun bindCurrencyRepo(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    fun bindSyncRepo(impl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    fun bindImageStorageRepo(impl: CloudinaryStorageRepositoryImpl): ImageStorageRepository

    @Binds
    @Singleton
    fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}