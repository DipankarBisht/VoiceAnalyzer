package com.example.voiceanalyzer

import android.net.Uri

data class AudioFile(
    val uri: Uri,
    val name: String,
    val durationMs: Long,
    val sizeInBytes: Long,
    val dateAdded: Long,
    val filePath: String = ""  // ← add this
)