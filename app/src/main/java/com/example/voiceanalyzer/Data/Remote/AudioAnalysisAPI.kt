package com.example.voiceanalyzer.Data.Remote

import com.example.voiceanalyzer.Data.Local.Analysis_data
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AudioAnalysisAPI {

        @Multipart
        @POST("analyze")
        suspend fun analyzeAudio(
            @Part file: MultipartBody.Part
        ): Analysis_data // Retrofit parses the JSON to AnalysisResult automatically!

}