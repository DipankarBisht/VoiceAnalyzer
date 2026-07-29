package com.example.voiceanalyzer.Data.Media

import com.example.voiceanalyzer.Data.Local.Analysis_data
import com.example.voiceanalyzer.Data.Remote.Retrofit_instance
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class Analysis_Repository {

    suspend fun analyzeAudio(audioPath: String): Analysis_data? {
        return try {
            val file = File(audioPath)
            if (!file.exists()) return null

            val requestFile = file.asRequestBody("audio/*".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            Retrofit_instance.apiService.analyzeAudio(multipartBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}