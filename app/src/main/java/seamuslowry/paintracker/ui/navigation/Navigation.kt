package seamuslowry.paintracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import seamuslowry.paintracker.R
import seamuslowry.paintracker.ui.screens.entry.EntryScreen
import seamuslowry.paintracker.ui.screens.report.ReportScreen

sealed class Screen(val identifier: String) {
    object Entry : Screen("entry")
    object Report : Screen("report")
}

data class NavBarData(
    val screen: Screen,
    val icon: ImageVector,
    val text: String,
)

@Composable
fun Navigation(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val navigableScreens = listOf(
        NavBarData(Screen.Entry, Icons.Filled.Assignment, stringResource(R.string.entry)),
        NavBarData(Screen.Report, Icons.Filled.DateRange, stringResource(R.string.report)),
    )

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
