package uk.ac.tees.mad.quotepro.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo: FirebaseAuthRepo,
) : ViewModel() {

    private val _navAction = MutableSharedFlow<SplashNavAction>()
    val navAction = _navAction.asSharedFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            authRepo.checkAuthStatus().collect { isAuth ->
                if (isAuth) {
                    _navAction.emit(SplashNavAction.NavigateToMain)
                } else {
                    _navAction.emit(SplashNavAction.NavigateToSignIn)
                }
            }
        }
    }

}