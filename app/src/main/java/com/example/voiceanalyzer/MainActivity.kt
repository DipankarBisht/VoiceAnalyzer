package com.example.voiceanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.voiceanalyzer.Data.Media.AudioRepository
import com.example.voiceanalyzer.Data.Media.HistoryManager
import com.example.voiceanalyzer.Data.Media.PlayerManager
import com.example.voiceanalyzer.Data.Media.VoiceRecdManager
import com.example.voiceanalyzer.ui.Navigation.MainNavHost
import com.example.voiceanalyzer.ui.theme.VoiceAnalyzerTheme

class MainActivity : ComponentActivity() {


    private lateinit var rcManger: VoiceRecdManager
    private lateinit var playerManager: PlayerManager
    private lateinit var repository: AudioRepository
    private lateinit var historyManager: HistoryManager


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        rcManger = VoiceRecdManager(applicationContext)
        playerManager = PlayerManager(applicationContext)
        repository = AudioRepository(applicationContext)
        historyManager = HistoryManager(applicationContext)

        setContent {
            VoiceAnalyzerTheme {
                MainNavHost(
                    rcManger = rcManger,
                    playerManager = playerManager,
                    repository = repository,
                    historyManager = historyManager

                )
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
