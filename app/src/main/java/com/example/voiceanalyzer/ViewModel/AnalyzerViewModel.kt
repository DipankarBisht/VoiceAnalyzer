package com.example.voiceanalyzer.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voiceanalyzer.Data.Media.Analysis_Repository
import com.example.voiceanalyzer.Data.Local.Analysis_data
import com.example.voiceanalyzer.Data.Local.HistoryEntity
import com.example.voiceanalyzer.Data.Media.HistoryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyzerViewModel (private val historyManager: HistoryManager) : ViewModel(){


    private val _result = MutableStateFlow<Analysis_data?>(null)
    val result = _result.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val history = _history.asStateFlow()

    private val _selectedHistoryResult = MutableStateFlow<Analysis_data?>(null)
    val selectedHistoryResult = _selectedHistoryResult.asStateFlow()

    private val repository = Analysis_Repository()

    init {
        viewModelScope.launch {
            historyManager.getAllHistory().collect { entities ->
                _history.value = entities
            }
        }
    }


    fun analysis_audiofile(audiofile_path: String){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            _selectedHistoryResult.value = null

            val analysisResult = repository.analyzeAudio(audiofile_path)

            if (analysisResult == null) {
                _errorMsg.value = "Analysis failed. Check server connection."
            } else {
                _result.value = analysisResult
                val fileName = audiofile_path.substringAfterLast("/")
                historyManager.saveAnalysis( fileName, analysisResult)
            }
            _isLoading.value = false
        }
    }

    fun  clearResult() {
        _result.value = null
    }
    fun selectHistoryItem(item: HistoryEntity) {
        _selectedHistoryResult.value = item.result
    }
    fun clearSelectedHistoryResult() {
        _selectedHistoryResult.value = null
    }
    fun deleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch { // 👈 Wrap it in a coroutine launch block
            historyManager.deleteItem(item.timestamp)
        }
    }


}