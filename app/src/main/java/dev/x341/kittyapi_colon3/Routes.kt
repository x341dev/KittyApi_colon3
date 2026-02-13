package dev.x341.kittyapi_colon3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Routes(val route: String) {
    object Splash : Routes("splash_screen")
    object Details : Routes("detail_screen")

    sealed class BottomNav(route: String, val icon: ImageVector, val label: String) : Routes(route) {
        object Home : BottomNav("home_screen", Icons.Default.Home, "Home")
        object Favorites : BottomNav("favorites_screen", Icons.Default.Favorite, "Favorites")
        object Settings : BottomNav("settings_screen", Icons.Default.Settings, "Settings")
    }
}