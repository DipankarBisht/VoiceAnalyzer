plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.example.voiceanalyzer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.voiceanalyzer"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // THIS BLOCK IS REQUIRED
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

     //THIS BLOCK IS ALSO REQUIRED FOR THE COMPOSER TO WORK
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Use the version matching your Kotlin plugin
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // This allows you to use icons like Icons.Default.Close
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    implementation("androidx.navigation:navigation-compose:2.9.0")


    // OkHttp for API calls
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
// Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
// Compose animations
    implementation("androidx.compose.animation:animation:1.6.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


}