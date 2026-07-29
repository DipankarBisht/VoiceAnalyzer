package com.example.voiceanalyzer.Data.Media

import android.content.Context
import com.example.voiceanalyzer.Data.Local.Analysis_data
import com.example.voiceanalyzer.Data.Local.Appdatabase
import com.example.voiceanalyzer.Data.Local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryManager(context: Context) {
    private val historyDao = Appdatabase.get_database(context).historydao()

    fun getAllHistory(): Flow<List<HistoryEntity>> {
        return historyDao.get_all_result()
    }

    suspend fun saveAnalysis(fileName: String, result: Analysis_data) {
        val entity = HistoryEntity(
            timestamp = System.currentTimeMillis(),
            fileName = fileName,
            result = result
        )
        historyDao.insert(entity)
    }

    suspend fun deleteItem(timestamp: Long) {
        historyDao.delete(timestamp)
    }
}