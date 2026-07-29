package com.example.voiceanalyzer.Data.Media

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlaybackState(
    val currentPositionMs: Int = 0,
    val totalDurationMs: Int = 0
)

class PlayerManager(private val context: Context) {

    private var player: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentUri = MutableStateFlow<Uri?>(null)
    val currentUri: StateFlow<Uri?> = _currentUri

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState
    
    fun play(uri: Uri) {
        stop()
        _currentUri.value = uri
        player = MediaPlayer.create(context, uri)?.apply {
            setOnCompletionListener {
                _isPlaying.value = false
                stopProgress()
                _playbackState.value = _playbackState.value.copy(
                    currentPositionMs = duration
                )
            }
            start()
            _isPlaying.value = true
            startProgress()
        }
    }

    fun pause() {
        player?.pause()
        _isPlaying.value = false
        stopProgress()
    }

    fun resume() {
        player?.start()
        _isPlaying.value = true
        startProgress()
    }

    fun stop() {
        stopProgress()
        try { player?.stop() } catch (e: Exception) { }
        player?.release()
        player = null
        _isPlaying.value = false
        _currentUri.value = null
        _playbackState.value = PlaybackState()
    }

    fun seekTo(ms: Int) {
        player?.seekTo(ms)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = ms)
    }

    private fun startProgress() {
        stopProgress()
        
        progressJob = scope.launch {
            while (isActive) {
                player?.let {
                    try {
                        _playbackState.value = PlaybackState(
                            currentPositionMs = it.currentPosition,
                            totalDurationMs = it.duration
                        )
                    } catch (e: Exception) { }
                }
                delay(100)
            }
        }
    }

    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stop()
        scope.cancel()
    }
}