package com.example.voiceanalyzer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voiceanalyzer.Data.Media.AudioRepository
import com.example.voiceanalyzer.Data.Media.PlayerManager
import com.example.voiceanalyzer.Data.Media.VoiceRecdManager
import com.example.voiceanalyzer.Data.Media.HistoryManager
import com.example.voiceanalyzer.ViewModel.AnalyzerViewModel
import com.example.voiceanalyzer.ViewModel.RecorderViewModel
import com.example.voiceanalyzer.ViewModel.RecordingViewModel

class AppViewModelFactory(
    private val rcManager: VoiceRecdManager,
    private val playerManager: PlayerManager,
    private val repository: AudioRepository,
    private val historyManager: HistoryManager,

    ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecorderViewModel::class.java) ->
                RecorderViewModel(rcManager) as T
            modelClass.isAssignableFrom(RecordingViewModel::class.java) ->
                RecordingViewModel(repository, playerManager) as T
            modelClass.isAssignableFrom(AnalyzerViewModel::class.java) ->
                AnalyzerViewModel(historyManager) as T // Change application to historyManager
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
