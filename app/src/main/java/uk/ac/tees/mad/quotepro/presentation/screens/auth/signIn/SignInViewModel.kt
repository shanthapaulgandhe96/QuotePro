package uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.domain.common.UiState
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignInUseCase
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _signInState = MutableStateFlow(SigInState())
    val signInState = _signInState.asStateFlow()

    private val _navAction = MutableSharedFlow<SignInNavAction>()
    val navAction = _navAction

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EmailChanged -> {
                _signInState.update { it.copy(email = event.value) }
            }

            is SignInEvent.PasswordChanged -> {
                _signInState.update { it.copy(password = event.value) }
            }

            SignInEvent.OnForgetPassword -> {
                viewModelScope.launch {
                    _navAction.emit(SignInNavAction.NavigateToForget)
                }
            }

            SignInEvent.OnSignIn -> {
                signIn()
            }

            SignInEvent.OnSignUp -> {
                viewModelScope.launch {
                    _navAction.emit(SignInNavAction.NavigateToSignUp)
                }
            }
        }
    }

    private fun signIn() {
        if (signInState.value.email.isBlank() || signInState.value.password.isBlank()) {
            _uiState.value = UiState.Error("All Fields are required!")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            signInUseCase.invoke(
                email = signInState.value.email,
                password = signInState.value.password
            ).onSuccess {
                _uiState.value = UiState.Success("SignIn Successfully!")
                _navAction.emit(SignInNavAction.NavigateToMain)
            }.onFailure {
                _uiState.value = UiState.Error(it.localizedMessage ?: "SignIn Failed!")
            }
        }
    }

    fun restUiState() {
        _uiState.value = UiState.Idle
    }


}