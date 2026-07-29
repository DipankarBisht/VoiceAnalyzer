package com.example.voiceanalyzer.Data.Local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class HistoryEntity(
    @PrimaryKey val timestamp: Long,
    val fileName: String,
    val result: Analysis_data
)
