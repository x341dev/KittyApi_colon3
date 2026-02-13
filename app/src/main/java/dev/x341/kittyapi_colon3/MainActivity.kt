package dev.x341.kittyapi_colon3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.x341.kittyapi_colon3.preferences.SettingsPreferences
import dev.x341.kittyapi_colon3.screens.CatDetailsScreen
import dev.x341.kittyapi_colon3.screens.FavouritesScreen
import dev.x341.kittyapi_colon3.screens.ListScreen
import dev.x341.kittyapi_colon3.screens.SettingsScreen
import dev.x341.kittyapi_colon3.screens.SplashScreen
import dev.x341.kittyapi_colon3.ui.theme.KittyApi_colon3Theme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.x341.kittyapi_colon3.viewmodel.CatViewModel
import dev.x341.kittyapi_colon3.viewmodel.CatViewModelFactory
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.navigationBars())
        enableEdgeToEdge()
        setContent {
            val prefs = remember { SettingsPreferences(applicationContext) }
            val isDarkMode = prefs.darkModeFlow.collectAsState(initial = false).value
            KittyApi_colon3Theme(darkTheme = isDarkMode) {
                // Surface general
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isCompactHeight = configuration.screenHeightDp < 600
    val sharedViewModel: CatViewModel = viewModel(factory = CatViewModelFactory(context))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.BottomNav.Home.route,
        Routes.BottomNav.Favorites.route,
        Routes.BottomNav.Settings.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),

        bottomBar = {
            if (showBottomBar) {

                BottomNavigationBar(navController, compact = isCompactHeight)
            }
        }
    ) { innerPadding ->
        NavigationGraph(navController = navController, viewModel = sharedViewModel, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, viewModel: CatViewModel, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.BottomNav.Home.route) {
            ListScreen(navController, viewModel)
        }

        composable(Routes.BottomNav.Favorites.route) {
            FavouritesScreen(navController, viewModel)
        }

        composable(Routes.BottomNav.Settings.route) {
            SettingsScreen(navController, viewModel)
        }

        composable(Routes.Details.route) {
            CatDetailsScreen(viewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, compact: Boolean = false) {
    val items = listOf(
        Routes.BottomNav.Home,
        Routes.BottomNav.Favorites,
        Routes.BottomNav.Settings
    )

    NavigationBar(
        modifier = if (compact) Modifier.height(44.dp) else Modifier,
        tonalElevation = if (compact) 0.dp else NavigationBarDefaults.Elevation,
        windowInsets = WindowInsets(0)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label, modifier = if (compact) Modifier.size(20.dp) else Modifier) },
                label = { if (!compact) Text(screen.label, style = MaterialTheme.typography.labelSmall) else null },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = !compact
            )
        }
    }
}