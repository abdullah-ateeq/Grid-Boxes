package com.example.gridboxes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gridboxes.ui.screens.AboutScreen
import com.example.gridboxes.ui.screens.GameScreen
import com.example.gridboxes.ui.screens.HomeScreen
import com.example.gridboxes.ui.screens.HowToPlayScreen
import com.example.gridboxes.ui.screens.ResultScreen
import com.example.gridboxes.ui.screens.SettingsScreen
import com.example.gridboxes.ui.screens.SplashScreen
import com.example.gridboxes.ui.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object HowToPlay : Screen("how_to_play")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@Composable
fun GridBoxesNavHost(
    viewModel: GameViewModel,
    navController: NavHostController = rememberNavController()
) {
    val state by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val stats by viewModel.stats.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                stats = stats,
                onPlayClick = {
                    if (state.isGameOver) {
                        viewModel.restartMatch()
                    }
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = { navController.navigate(Screen.HowToPlay.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                state = state,
                canUndo = viewModel.canUndo,
                showLastBorder = settings.showLastBorder,
                showBorderGuides = settings.showBorderGuides,
                onBorderTapped = { border -> viewModel.onBorderTapped(border) },
                onUndoClick = { viewModel.undo() },
                onRestartClick = { viewModel.restartMatch() },
                onHomeClick = {
                    viewModel.restartMatch()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                state = state,
                onPlayAgainClick = {
                    viewModel.restartMatch()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHomeClick = {
                    viewModel.restartMatch()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onThemeModeChange = { mode -> viewModel.setThemeMode(mode) },
                onBoardThemeChange = { theme -> viewModel.setBoardTheme(theme) },
                onSoundChange = { enabled -> viewModel.setSoundEnabled(enabled) },
                onHapticsChange = { enabled -> viewModel.setHapticsEnabled(enabled) },
                onShowLastBorderChange = { enabled -> viewModel.setShowLastBorder(enabled) },
                onShowBorderGuidesChange = { enabled -> viewModel.setShowBorderGuides(enabled) },
                onResetStats = { viewModel.resetStatistics() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
