package uk.ac.tees.mad.quotepro.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.data.repo.QuoteRepositoryImpl
import uk.ac.tees.mad.quotepro.domain.repo.QuoteRepository
import uk.ac.tees.mad.quotepro.domain.usecase.auth.ResetPasswordUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignInUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignUpUseCase
import uk.ac.tees.mad.quotepro.domain.usecase.quote.AddQuoteUseCase
import uk.ac.tees.mad.quotepro.presentation.screens.auth.forget.ForgetViewModel
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn.SignInViewModel
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp.SignUpViewModel
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteViewModel

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSignUpUseCase(signUpUseCase: SignUpUseCase) =
        SignUpViewModel(signUpUseCase = signUpUseCase)

    @Provides
    fun provideSignInUseCase(signInUseCase: SignInUseCase) = SignInViewModel(signInUseCase)

    @Provides
    fun provideForgetUseCase(resetPasswordUseCase: ResetPasswordUseCase) =
        ForgetViewModel(resetPasswordUseCase)

    @Provides
    fun provideAddQuoteUseCase(repository: QuoteRepositoryImpl): AddQuoteUseCase {
        return AddQuoteUseCase(repository)
    }

}