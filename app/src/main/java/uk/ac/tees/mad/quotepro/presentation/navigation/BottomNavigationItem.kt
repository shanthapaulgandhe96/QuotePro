package uk.ac.tees.mad.quotepro.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(
    val route: Any,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object SavedQuotes : BottomNavigationItem(
        route = SavedQuotesRoute,
        title = "Quotes",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    )

    object Reminders : BottomNavigationItem(
        route = ReminderRoute,
        title = "Reminders",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    object Profile : BottomNavigationItem(
        route = ProfileRoute,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

val bottomNavigationItems = listOf(
    BottomNavigationItem.SavedQuotes,
    BottomNavigationItem.Reminders,
    BottomNavigationItem.Profile
)