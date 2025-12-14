package uk.ac.tees.mad.quotepro.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.quotepro.presentation.screens.profile.ProfileScreen
import uk.ac.tees.mad.quotepro.presentation.screens.reminder.ReminderScreen
import uk.ac.tees.mad.quotepro.presentation.screens.saveQuotes.SavedQuotesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavigationItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == item.route::class.qualifiedName
                    } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(text = item.title) },
                        alwaysShowLabel = false
                    )
                }
            }
        }
        // FAB removed from here
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = SavedQuotesRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<SavedQuotesRoute> {
                SavedQuotesScreen(navController = navController)
            }

            composable<ReminderRoute> {
                ReminderScreen(navController = navController)
            }

            composable<ProfileRoute> {
                ProfileScreen(navController = navController)
            }
        }
    }
}