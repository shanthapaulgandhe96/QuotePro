package uk.ac.tees.mad.quotepro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import uk.ac.tees.mad.quotepro.presentation.screens.auth.forget.ForgetScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn.SignInScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp.SignUpScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.reminder.ReminderScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.saveQuotes.SavedQuotesScreen
import uk.ac.tees.mad.quotepro.presentation.screens.main.settings.SettingsScreen
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteScreen
import uk.ac.tees.mad.quotepro.presentation.screens.quotedetail.QuoteDetailScreen
import uk.ac.tees.mad.quotepro.presentation.screens.splash.SplashScreen

@Composable
fun QuoteProNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {

        composable<SplashRoute> {
            SplashScreen(navController, hiltViewModel())
        }

        composable<SignInRoute> {
            SignInScreen(navController, hiltViewModel())
        }

        composable<SignUpRote> {
            SignUpScreen(navController, hiltViewModel())
        }

        composable<ForgetRoute> {
            ForgetScreen(navController, hiltViewModel())
        }

        navigation<MainRoute>(startDestination = SavedQuotesRoute) {
            composable<SavedQuotesRoute> {
                SavedQuotesScreen(navController)
            }
            composable<ReminderRoute> {
                ReminderScreen(navController)
            }
            composable<SettingsRoute> {
                SettingsScreen(navController)
            }
        }

        composable<NewQuoteRoute> {
            NewQuoteScreen(navController)
        }

        // Quote Detail Route with quoteId parameter
        composable<QuoteDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<QuoteDetailRoute>()
            QuoteDetailScreen(
                navController = navController,
                quoteId = args.quoteId
            )
        }

        // Edit Quote Route (reuses NewQuoteScreen with quoteId)
        composable<EditQuoteRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<EditQuoteRoute>()
            NewQuoteScreen(
                navController = navController,
                quoteId = args.quoteId
            )
        }
    }
}