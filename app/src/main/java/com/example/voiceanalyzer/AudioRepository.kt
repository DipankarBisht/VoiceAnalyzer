package com.example.voiceanalyzer

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import java.io.File

class AudioRepository(private val context: Context) {

    fun fetchSavedRecordings(): List<AudioFile> {
        val audioList = mutableListOf<AudioFile>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA        // ← add this (actual file path)
        )

        val selection = "${MediaStore.Audio.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Music/VoiceRecorder%")
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn       = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val dateColumn     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dataColumn     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA) // ← add this

            while (cursor.moveToNext()) {
                val id       = cursor.getLong(idColumn)
                val name     = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn)
                val size     = cursor.getLong(sizeColumn)
                val date     = cursor.getLong(dateColumn) * 1000
                val filePath = cursor.getString(dataColumn) ?: "" // ← add this

                val contentUri = ContentUris.withAppendedId(collection, id)

                audioList.add(
                    AudioFile(
                        uri         = contentUri,
                        name        = name,
                        durationMs  = duration,
                        sizeInBytes = size,
                        dateAdded   = date,
                        filePath    = filePath  // ← add this
                    )
                )
            }
        }
        return audioList
    }

    fun deleteRecording(audioFile: AudioFile): Boolean {
        return try {
            context.contentResolver.delete(audioFile.uri, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun renameRecording(audioFile: AudioFile, newName: String): Boolean {
        return try {
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, "$newName.m4a")
            }
            context.contentResolver.update(audioFile.uri, values, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }
}