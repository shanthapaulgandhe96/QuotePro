package uk.ac.tees.mad.quotepro.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepo
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val user = authRepo.getCurrentUser()
            user?.let {
                _state.update { state ->
                    state.copy(
                        userName = it.displayName ?: "User",
                        userEmail = it.email ?: "No email"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepo.signOut()
                _effect.emit(ProfileEffect.ShowToast("Logged out successfully"))
                _effect.emit(ProfileEffect.NavigateToSignIn)
            } catch (e: Exception) {
                _effect.emit(ProfileEffect.ShowToast("Logout failed: ${e.message}"))
            }
        }
    }
}

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false
)

sealed class ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect()
    object NavigateToSignIn : ProfileEffect()
}