package uk.ac.tees.mad.quotepro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.quotepro.presentation.screens.auth.forget.ForgetScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn.SignInScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp.SignUpScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.reminder.ReminderScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.SavedQuotes
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.SavedQuotesScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.settings.SettingsScreen
import uk.ac.tees.mad.quotepro.presentation.screens.splash.SplashScreen

@Composable
fun QuoteProNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ""
    ) {

        composable<SplashRoute>{
            SplashScreen(navController)
        }

        composable<SignInRoute> {
            SignInScreen(navController)
        }

        composable<SignUpRote> {
            SignUpScreen(navController)
        }

        composable<ForgetRoute> {
            ForgetScreen(navController)
        }

        navigation<MainRoute>(startDestination = SavedQuotesRoute){
            composable<SavedQuotesRoute> {
                SavedQuotesScreen(navController)
            }
            composable<ReminderRoute> {
                ReminderScreen(navController)
            }
            composable <SettingsRoute>{
                SettingsScreen(navController)
            }
        }

        composable<NewQuoteRoute> {
            NewQuoteRoute
        }

    }

}