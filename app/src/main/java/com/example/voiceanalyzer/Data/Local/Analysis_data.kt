package com.example.voiceanalyzer.Data.Local

import androidx.room.Entity

@Entity(tableName = "Aanlysis_Data")
data class Analysis_data(
val overall_score: Float,
val overall_grade: String,
val transcription: String,
val duration_seconds: Float,
val clarity: ScoreDetail,
val fluency: FluencyDetail,
val grammar: GrammarDetail,
val confidence: ConfidenceDetail,
val emotional_tone: EmotionalTone
)

@Entity(tableName = "Aanlysis_Data")
data class ScoreDetail(
    val score: Float,
    val grade: String,
    val feedback: String
)

@Entity(tableName = "Aanlysis_Data")
data class FluencyDetail(
    val score: Float,
    val grade: String,
    val wpm: Float,
    val filler_count: Int,
    val pause_count: Int,
    val feedback: String
)

data class GrammarDetail(
    val score: Float,
    val grade: String,
    val error_count: Int,
    val feedback: String
)

data class ConfidenceDetail(
    val score: Float,
    val grade: String,
    val pitch_stability: Float,
    val silence_score: Float,
    val feedback: String
)

data class EmotionalTone(
    val tone: String,
    val confidence: Float,
    val suggestion: String
)

