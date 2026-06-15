package com.example.voiceanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.voiceanalyzer.ui.theme.VoiceAnalyzerTheme

class MainActivity : ComponentActivity() {

    private lateinit var rcManger: VoiceRecdManager
    private lateinit var playerManager: PlayerManager
    private lateinit var repository: AudioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        rcManger = VoiceRecdManager(applicationContext)
        playerManager = PlayerManager(applicationContext)
        repository = AudioRepository(applicationContext)

        setContent {
            VoiceAnalyzerTheme {
                MainNavHost(
                    rcManger = rcManger,
                    playerManager = playerManager,
                    repository = repository
                )
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}