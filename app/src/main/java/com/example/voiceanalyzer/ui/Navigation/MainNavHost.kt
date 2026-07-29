package com.example.voiceanalyzer.ui.Navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voiceanalyzer.Data.Media.AudioRepository
import com.example.voiceanalyzer.Data.Media.PlayerManager
import com.example.voiceanalyzer.Data.Media.VoiceRecdManager
import com.example.voiceanalyzer.Data.Media.HistoryManager
import com.example.voiceanalyzer.ViewModel.AnalyzerViewModel
import com.example.voiceanalyzer.ViewModel.RecorderViewModel
import com.example.voiceanalyzer.ViewModel.RecordingViewModel
import com.example.voiceanalyzer.ui.Screens.AnalyzerScreen
import com.example.voiceanalyzer.ui.Screens.RecorderScreen
import com.example.voiceanalyzer.ui.Screens.RecordingsScreen
import com.example.voiceanalyzer.viewmodel.AppViewModelFactory
import java.net.URLDecoder

@Composable
fun MainNavHost(
    rcManger: VoiceRecdManager,
    playerManager: PlayerManager,
    repository: AudioRepository,
    historyManager: HistoryManager
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val factory = remember(rcManger, playerManager, repository, historyManager) {
        AppViewModelFactory(rcManger, playerManager, repository, historyManager)
    }
    val recorderViewModel: RecorderViewModel = viewModel(factory = factory)
    val recordingsViewModel: RecordingViewModel = viewModel(factory = factory)
    val analyzerViewModel: AnalyzerViewModel = viewModel(factory = factory)

    val navController = rememberNavController()
    val tabs = listOf(Screen.Recorder, Screen.Recordings, Screen.Analyzer)
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0C0A1B),tonalElevation = 0.dp) {
                tabs.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(
                            screen.route.substringBefore("{")
                        ) == true,
                        onClick = {
                            navController.navigate(
                                if (screen is Screen.Analyzer)
                                    "analyzer/none"
                                else screen.route
                            ) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6C63FF), // Electric Purple
                            selectedTextColor = Color(0xFF6C63FF),
                            indicatorColor = Color(0xFF1F1A3A),    // Translucent Purple Pill
                            unselectedIconColor = Color(0xFF7E7A94), // Muted Gray
                            unselectedTextColor = Color(0xFF7E7A94)
                    )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Recorder.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Recorder.route) {
                RecorderScreen(recorderViewModel)
            }

            composable(Screen.Recordings.route) {
                RecordingsScreen(
                    recordingsViewModel,
                    onAnalyzeClick = { filePath ->
                        navController.navigate(
                            Screen.Analyzer.createRoute(filePath)
                        )
                    }
                )
            }

            composable(
                route = Screen.Analyzer.route,
                arguments = listOf(
                    navArgument("filePath") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val encoded = backStackEntry.arguments?.getString("filePath") ?: "none"
                val filePath = if (encoded == "none") null
                else URLDecoder.decode(encoded, "UTF-8")
                AnalyzerScreen(audioFilePath = filePath,analyzerViewModel )
            }
        }
    }
}