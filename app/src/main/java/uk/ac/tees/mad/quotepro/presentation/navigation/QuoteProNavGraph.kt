package uk.ac.tees.mad.quotepro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import uk.ac.tees.mad.quotepro.presentation.screens.auth.forget.ForgetScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signIn.SignInScreen
import uk.ac.tees.mad.quotepro.presentation.screens.auth.signUp.SignUpScreen
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.NewQuoteScreen
import uk.ac.tees.mad.quotepro.presentation.screens.quotedetail.QuoteDetailScreen
import uk.ac.tees.mad.quotepro.presentation.screens.splash.SplashScreen

@Composable
fun QuoteProNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        // Splash Screen
        composable<SplashRoute> {
            SplashScreen(navController)
        }

        // Auth Screens
        composable<SignInRoute> {
            SignInScreen(navController)
        }

        composable<SignUpRote> {
            SignUpScreen(navController)
        }

        composable<ForgetRoute> {
            ForgetScreen(navController)
        }

        // Main Screen with Bottom Navigation
        composable<MainRoute> {
            MainScreen(navController = navController)
        }

        // New Quote Screen
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