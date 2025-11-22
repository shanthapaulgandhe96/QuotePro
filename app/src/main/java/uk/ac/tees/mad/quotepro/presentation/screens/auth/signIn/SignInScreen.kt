package uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.domain.common.UiState
import uk.ac.tees.mad.quotepro.presentation.navigation.ForgetRoute
import uk.ac.tees.mad.quotepro.presentation.navigation.MainRoute
import uk.ac.tees.mad.quotepro.presentation.navigation.SignUpRote
import uk.ac.tees.mad.quotepro.utils.showToast

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                val successMessage = (uiState as UiState.Success).message
                context.showToast(successMessage)
                viewModel.restUiState()
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                context.showToast(errorMessage)
                viewModel.restUiState()
            }

            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navAction.collect { navAction ->
            when (navAction) {
                SignInNavAction.NavigateToForget -> navController.navigate(ForgetRoute)
                SignInNavAction.NavigateToMain -> navController.navigate(MainRoute)
                SignInNavAction.NavigateToSignUp -> navController.navigate(SignUpRote)
            }
        }
    }


    SignInContent(viewModel, uiState)
}


@Composable
fun SignInContent(viewModel: SignInViewModel, uiState: UiState) {

    val focusManager = LocalFocusManager.current
    val signInState by viewModel.signInState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SignIn",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = signInState.email,
            onValueChange = {
                viewModel.onEvent(SignInEvent.EmailChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Enter your email")
            },
            label = {
                Text(
                    text = "Email"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = signInState.password,
            onValueChange = {
                viewModel.onEvent(SignInEvent.PasswordChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter your password") },
            label = {
                Text(text = "Password")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(
                onClick = {
                    viewModel.onEvent(SignInEvent.OnForgetPassword)
                }
            ) {
                Text("Forget Password?")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onEvent(SignInEvent.OnSignIn)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            AnimatedContent(
                targetState = uiState is UiState.Loading
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SignIn"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))

        OutlinedButton(
            onClick = {
                viewModel.onEvent(SignInEvent.OnSignUp)

            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = "SignUp"
            )
        }
    }
}


