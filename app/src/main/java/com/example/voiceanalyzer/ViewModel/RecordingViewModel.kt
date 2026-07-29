package com.example.voiceanalyzer.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voiceanalyzer.Data.Media.AudioFile
import com.example.voiceanalyzer.Data.Media.AudioRepository
import com.example.voiceanalyzer.Data.Media.PlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordingViewModel(
    private val audioRepository: AudioRepository,
    private val playerManager: PlayerManager
) : ViewModel() {



    val isplaying_audio = playerManager.isPlaying
    val current_uri = playerManager.currentUri
    val playbackstate = playerManager.playbackState

    private val _recording = MutableStateFlow<List<AudioFile>>(emptyList())
    val recording = _recording.asStateFlow()

    init {
        load_recordings()
    }

    fun load_recordings(){
        viewModelScope.launch {
            _recording.value = audioRepository.fetchSavedRecordings()
        }
    }

    fun play_audio(uri : Uri){
        playerManager.play(uri)
    }

    fun pause_audio(){
        playerManager.pause()
    }

    fun resume_audio(){
        playerManager.resume()
    }

    fun seekto(position: Int){
        playerManager.seekTo(position)
    }

    fun stop_audio(){
        playerManager.stop()
    }

    fun delete_audio(audioFile: AudioFile){
        viewModelScope.launch {

            if (current_uri==audioFile.uri) {
                playerManager.stop()
            }
            audioRepository.deleteRecording(audioFile)
            load_recordings()

        }

    }

    fun rename_audio(audioFile: AudioFile , new_name : String){
        viewModelScope.launch {
            audioRepository.renameRecording(audioFile,new_name)
            load_recordings()
        }

    }





}