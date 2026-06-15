package com.example.voiceanalyzer

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Recorder   : Screen("recorder",   "Recorder",   android.R.drawable.ic_btn_speak_now)
    object Recordings : Screen("recordings", "Recordings", android.R.drawable.ic_menu_agenda)
    object Analyzer   : Screen(
        "analyzer/{filePath}",
        "Analyzer",
        android.R.drawable.ic_menu_info_details
    ) {
        fun createRoute(filePath: String): String {
            // encode the path so slashes don't break navigation
            val encoded = java.net.URLEncoder.encode(filePath, "UTF-8")
            return "analyzer/$encoded"
        }
    }
}