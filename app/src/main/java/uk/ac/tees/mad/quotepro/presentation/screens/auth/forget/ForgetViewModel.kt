package uk.ac.tees.mad.quotepro.presentation.screens.auth.forget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.domain.common.UiState
import uk.ac.tees.mad.quotepro.domain.usecase.auth.ResetPasswordUseCase
import javax.inject.Inject

@HiltViewModel
class ForgetViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _forgetState = MutableStateFlow(ForgetState())
    val forgetState = _forgetState.asStateFlow()

    private val _navAction = MutableSharedFlow<ForgetNavAction>()
    val navAction = _navAction.asSharedFlow()

    fun onEvent(event: ForgetEvent) {
        when (event) {
            is ForgetEvent.EmailChanged -> {
                _forgetState.update { it.copy(email = event.email) }
            }

            ForgetEvent.OnResetForgetPassword -> {
                resetPassword()
            }

            ForgetEvent.OnSigIn -> {
                viewModelScope.launch {
                    _navAction.emit(ForgetNavAction.NavigateToSignIn)
                }
            }
        }
    }

    private fun resetPassword() {
        if (forgetState.value.email.isBlank()) {
            _uiState.value = UiState.Error("Email are required!")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            resetPasswordUseCase.invoke(
                email = forgetState.value.email
            ).onSuccess {
                _uiState.value = UiState.Success("Password Reset Successfully!")
            }.onFailure {
                _uiState.value = UiState.Error(it.localizedMessage ?: "Password Reset Failed!")
            }
        }
    }

    fun restUiState() {
        _uiState.value = UiState.Idle
    }


}