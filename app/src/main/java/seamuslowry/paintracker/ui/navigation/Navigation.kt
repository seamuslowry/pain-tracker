package seamuslowry.paintracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import seamuslowry.paintracker.ui.screens.configuration.ConfigurationScreen
import seamuslowry.paintracker.ui.screens.entry.EntryScreen
import seamuslowry.paintracker.ui.screens.report.ReportScreen

sealed class Screen(val identifier: String) {
    object Configuration : Screen("configuration")
    object Entry : Screen("entry")
    object Report : Screen("report")
}

data class NavBarData(
    val screen: Screen,
    val icon: ImageVector,
    val text: String,
)

val navigableScreens = listOf(
    NavBarData(Screen.Entry, Icons.Filled.Assignment, "Entry"),
    NavBarData(Screen.Report, Icons.Filled.DateRange, "Report"),
    NavBarData(Screen.Configuration, Icons.Filled.Settings, "Settings"),
)

@Composable
fun Navigation(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                navigableScreens.map {
                    NavigationBarItem(
                        icon = { Icon(it.icon, contentDescription = it.text) },
                        selected = currentRoute == it.screen.identifier,
                        onClick = { navController.navigate(it.screen.identifier) },
                    )
                }
            }
        },
    ) {
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(
                Screen.Configuration.identifier,
            ) {
                ConfigurationScreen()
            }
            composable(
                Screen.Entry.identifier,
            ) {
                EntryScreen()
            }
            composable(
                Screen.Report.identifier,
            ) {
                ReportScreen()
            }
        }
    }
}
