package uk.ac.tees.mad.quotepro.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object SignInRoute

@Serializable
object SignUpRote

@Serializable
object ForgetRoute

//Main Routes

@Serializable
object MainRoute

@Serializable
object SavedQuotesRoute

@Serializable
object ReminderRoute

@Serializable
object SettingsRoute

//Other Routes

@Serializable
object NewQuoteRoute

@Serializable
data class QuoteDetailRoute(val quoteId: String)

@Serializable
data class EditQuoteRoute(val quoteId: String)