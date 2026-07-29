package com.example.voiceanalyzer.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.voiceanalyzer.Data.Media.VoiceRecdManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecorderViewModel(private val voicerecdManager : VoiceRecdManager) : ViewModel() {


    val amplitude = voicerecdManager.am

    val recordingTime = voicerecdManager.rectime


    private val _isRecording =  MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _isplay = MutableStateFlow(false)
    val isplay : StateFlow<Boolean> = _isplay.asStateFlow()

    fun start_recording(context: Context){
        voicerecdManager.strtrecod(context)
        _isRecording.value = true
        _isplay.value = true
    }

    fun stop_recording(context: Context){
        voicerecdManager.stoprecod(context)
        _isRecording.value = false
        _isplay.value = false
    }

    fun pause_recording(){
        voicerecdManager.paus()
        _isplay.value = false
    }

    fun resume_recording(){
        voicerecdManager.resme()
        _isplay.value = true
    }

    fun discard_recording(context: Context){
        voicerecdManager.dismiss(context)
        _isRecording.value= false
        _isplay.value = false

    }
}