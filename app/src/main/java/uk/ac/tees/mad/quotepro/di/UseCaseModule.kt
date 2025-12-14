package uk.ac.tees.mad.quotepro.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.domain.repo.CurrencyRepository
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import uk.ac.tees.mad.quotepro.domain.repo.ImageStorageRepository
import uk.ac.tees.mad.quotepro.domain.repo.NewQuoteRepo
import uk.ac.tees.mad.quotepro.domain.repo.SyncRepository
import uk.ac.tees.mad.quotepro.domain.usecase.auth.ResetPasswordUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignInUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignUpUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.currency.ConvertAmountUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.currency.GetExchangeRatesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.DeleteQuoteUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.FilterQuotesByStatusUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetAllQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.GetQuoteByIdUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SaveQuoteUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SearchQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SyncOfflineQuotesUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.UpdateQuoteStatusUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.storage.DeleteImageUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.storage.UploadImageUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Auth Use Cases
    @Provides
    @Singleton
    fun provideSignUpUseCase(repository: FirebaseAuthRepo): SignUpUseCase {
        return SignUpUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSignInUseCase(repository: FirebaseAuthRepo): SignInUseCase {
        return SignInUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(repository: FirebaseAuthRepo): ResetPasswordUseCase {
        return ResetPasswordUseCase(repository)
    }

    // Quote Use Cases
    @Provides
    @Singleton
    fun provideSaveQuoteUseCase(repository: NewQuoteRepo): SaveQuoteUseCase {
        return SaveQuoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetQuoteByIdUseCase(repository: NewQuoteRepo): GetQuoteByIdUseCase {
        return GetQuoteByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllQuotesUseCase(repository: NewQuoteRepo): GetAllQuotesUseCase {
        return GetAllQuotesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteQuoteUseCase(repository: NewQuoteRepo): DeleteQuoteUseCase {
        return DeleteQuoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateQuoteStatusUseCase(repository: NewQuoteRepo): UpdateQuoteStatusUseCase {
        return UpdateQuoteStatusUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchQuotesUseCase(): SearchQuotesUseCase {
        return SearchQuotesUseCase()
    }

    @Provides
    @Singleton
    fun provideFilterQuotesByStatusUseCase(): FilterQuotesByStatusUseCase {
        return FilterQuotesByStatusUseCase()
    }

    // Currency Use Cases
    @Provides
    @Singleton
    fun provideGetExchangeRatesUseCase(repository: CurrencyRepository): GetExchangeRatesUseCase {
        return GetExchangeRatesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideConvertAmountUseCase(repository: CurrencyRepository): ConvertAmountUseCase {
        return ConvertAmountUseCase(repository)
    }

    // Sync Use Cases
    @Provides
    @Singleton
    fun provideSyncOfflineQuotesUseCase(repository: SyncRepository): SyncOfflineQuotesUseCase {
        return SyncOfflineQuotesUseCase(repository)
    }

    // Storage Use Cases (NEW)
    @Provides
    @Singleton
    fun provideUploadImageUseCase(repository: ImageStorageRepository): UploadImageUseCase {
        return UploadImageUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteImageUseCase(repository: ImageStorageRepository): DeleteImageUseCase {
        return DeleteImageUseCase(repository)
    }
}