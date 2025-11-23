package uk.ac.tees.mad.quotepro.presentation.screens.splash

sealed class SplashNavAction {
    object NavigateToSignIn: SplashNavAction()
    object NavigateToMain: SplashNavAction()
}