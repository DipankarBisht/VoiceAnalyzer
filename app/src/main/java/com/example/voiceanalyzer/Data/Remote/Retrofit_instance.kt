package com.example.voiceanalyzer.Data.Remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue


object Retrofit_instance {

    private val BASE_URL = "https://speech-analyzer-854674223155.asia-south1.run.app/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
    val apiService: AudioAnalysisAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Automatic Gson conversion
            .build()
            .create(AudioAnalysisAPI::class.java)
    }
}
