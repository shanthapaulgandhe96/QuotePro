package uk.ac.tees.mad.quotepro.presentation.screens.profile

sealed class ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect()
    object NavigateToSignIn : ProfileEffect()
    object RestartApp : ProfileEffect() // Useful for theme changes if needed
}