package com.example.voiceanalyzer.Data.Local

import androidx.room.TypeConverter
import com.google.gson.Gson

class RoomTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromAnalysisData(data: Analysis_data): String {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toAnalysisData(json: String): Analysis_data {
        return gson.fromJson(json, Analysis_data::class.java)
    }
}
