package com.example.voiceanalyzer

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class HistoryItem(
    val fileName: String,
    val timestamp: Long,
    val result: AnalysisResult
)

object HistoryManager {
    private const val PREFS_NAME = "analysis_history"
    private const val KEY_HISTORY = "history_list"

    fun saveAnalysis(context: Context, fileName: String, result: AnalysisResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getHistory(context).toMutableList()

        current.add(0, HistoryItem(fileName, System.currentTimeMillis(), result))

        val json = Gson().toJson(current)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }

    fun getHistory(context: Context): List<HistoryItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()

        val type = object : TypeToken<List<HistoryItem>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteItem(context: Context, timestamp: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getHistory(context).filter { it.timestamp != timestamp }
        val json = Gson().toJson(current)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }
}