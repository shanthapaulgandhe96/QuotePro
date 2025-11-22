package uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn

sealed class SignInNavAction {
    object NavigateToForget : SignInNavAction()
    object NavigateToMain : SignInNavAction()
    object NavigateToSignUp : SignInNavAction()
}