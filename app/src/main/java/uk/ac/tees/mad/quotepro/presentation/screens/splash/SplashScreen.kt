package uk.ac.tees.mad.quotepro.presentation.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import uk.ac.tees.mad.quotepro.presentation.navigation.MainRoute
import uk.ac.tees.mad.quotepro.presentation.navigation.SignInRoute
import uk.ac.tees.mad.quotepro.presentation.screens.auth.forget.ForgetNavAction
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn.SignInViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        viewModel.navAction.collect { navAction ->
            when (navAction) {
                SplashNavAction.NavigateToMain -> navController.navigate(MainRoute)
                SplashNavAction.NavigateToSignIn -> navController.navigate(SignInRoute)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.RequestQuote,
            contentDescription = "",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }

}
