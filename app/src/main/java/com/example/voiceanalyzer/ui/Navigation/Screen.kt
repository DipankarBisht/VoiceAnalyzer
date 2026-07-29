package com.example.voiceanalyzer.ui.Navigation

import android.R
import java.net.URLEncoder

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Recorder   : Screen("recorder",   "Recorder",   R.drawable.ic_btn_speak_now)
    object Recordings : Screen("recordings", "Recordings", R.drawable.ic_menu_agenda)
    object Analyzer   : Screen(
        "analyzer/{filePath}",
        "Analyzer",
        R.drawable.ic_menu_info_details
    ) {
        fun createRoute(filePath: String): String {
            // encode the path so slashes don't break navigation
            val encoded = URLEncoder.encode(filePath, "UTF-8")
            return "analyzer/$encoded"
        }
    }
}