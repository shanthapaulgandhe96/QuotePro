package uk.ac.tees.mad.quotepro.presentation.screens.auth.forget

sealed class ForgetEvent {
    data class EmailChanged(val email: String): ForgetEvent()
    object OnResetForgetPassword: ForgetEvent()
    object OnSigIn: ForgetEvent()
}