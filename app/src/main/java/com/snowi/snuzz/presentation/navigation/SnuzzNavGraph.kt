package com.snowi.snuzz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.snowi.snuzz.presentation.features.home.HomeScreen
import com.snowi.snuzz.presentation.features.notification.NotificationScreen
import com.snowi.snuzz.presentation.features.history.HistoryScreen
import com.snowi.snuzz.presentation.features.profile.ProfileScreen
import com.snowi.snuzz.presentation.features.preferences.SavedPreferencesScreen

@Composable
fun SnuzzNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("notification") { NotificationScreen() }
        composable("history") { HistoryScreen() }

        // Profile → provides navigation lambda to Saved Preferences
        composable("profile") {
            ProfileScreen(
                onManageSavedPreferences = {
                    navController.navigate("saved_preferences")
                }
            )
        }

        // NEW: dedicated screen for saved preferences
        composable("saved_preferences") {
            SavedPreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}