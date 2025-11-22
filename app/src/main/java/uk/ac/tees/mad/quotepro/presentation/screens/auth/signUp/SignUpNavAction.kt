package uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp

sealed class SignUpNavAction {
    object NavigateToSignIn : SignUpNavAction()
    object NavigateToMain : SignUpNavAction()
}