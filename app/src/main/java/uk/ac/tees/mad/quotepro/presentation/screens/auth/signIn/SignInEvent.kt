package uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn

import uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp.SignUpUiEvent

sealed class SignInEvent {
    data class EmailChanged(val value: String) : SignInEvent()
    data class PasswordChanged(val value: String) : SignInEvent()
    object OnSignIn : SignInEvent()
    object OnForgetPassword : SignInEvent()
    object OnSignUp : SignInEvent()
}