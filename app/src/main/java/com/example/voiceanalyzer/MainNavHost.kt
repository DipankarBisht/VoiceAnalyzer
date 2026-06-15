package com.example.voiceanalyzer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLDecoder

@Composable
fun MainNavHost(
    rcManger: VoiceRecdManager,
    playerManager: PlayerManager,
    repository: AudioRepository
) {
    val navController = rememberNavController()
    val tabs = listOf(Screen.Recorder, Screen.Recordings, Screen.Analyzer)
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
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
                        label = { Text(screen.label) }
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
                RecorderScreen(rcManger = rcManger)
            }

            composable(Screen.Recordings.route) {
                RecordingsScreen(
                    playerManager = playerManager,
                    repository    = repository,
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
                AnalyzerScreen(audioFilePath = filePath)
            }
        }
    }
}