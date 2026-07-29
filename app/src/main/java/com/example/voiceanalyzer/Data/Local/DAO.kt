package com.example.voiceanalyzer.Data.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO_database {

    @Query("SELECT * FROM analysis_history order BY timestamp DESC")
    fun get_all_result(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history : HistoryEntity)

    @Query("DELETE FROM ANALYSIS_HISTORY WHERE timestamp = :timestam")
    suspend fun delete(timestam: Long)
}