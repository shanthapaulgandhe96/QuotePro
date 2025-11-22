package uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp

sealed class SignUpUiEvent {
    data class NameChanged(val value: String) : SignUpUiEvent()
    data class EmailChanged(val value: String) : SignUpUiEvent()
    data class PasswordChanged(val value: String) : SignUpUiEvent()
    object OnSignUp : SignUpUiEvent()
    object OnSignIn : SignUpUiEvent()
}