package uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp

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
import uk.ac.tees.mad.quotepro.domain.usecase.auth.SignUpUseCase
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState

    private val _navAction = MutableSharedFlow<SignUpNavAction>()
    val navAction = _navAction.asSharedFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.NameChanged -> {
                _signUpState.update { it.copy(name = event.value) }
            }

            is SignUpUiEvent.EmailChanged -> {
                _signUpState.update { it.copy(email = event.value) }
            }

            is SignUpUiEvent.PasswordChanged -> {
                _signUpState.update { it.copy(password = event.value) }
            }

            SignUpUiEvent.OnSignIn -> {
                viewModelScope.launch {
                    _navAction.emit(SignUpNavAction.NavigateToSignIn)
                }
            }

            SignUpUiEvent.OnSignUp -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        if (signUpState.value.email.isBlank() || signUpState.value.password.isBlank() || signUpState.value.name.isBlank()) {
            _uiState.value = UiState.Error("All Fields are required!")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            signUpUseCase.invoke(
                name = signUpState.value.name,
                email = signUpState.value.email,
                password = signUpState.value.password
            ).onSuccess {
                _uiState.value = UiState.Success("SignUp Successfully!")
                _navAction.emit(SignUpNavAction.NavigateToMain)
            }.onFailure {
                _uiState.value = UiState.Error(it.localizedMessage ?: "SignUp Failed!")
            }
        }
    }

    fun restUiState() {
        _uiState.value = UiState.Idle
    }

}