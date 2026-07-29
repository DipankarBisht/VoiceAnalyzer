plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.voiceanalyzer"
    compileSdk = 37

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
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)

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
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Retrofit Gson Converter (handles automatic JSON-to-Object parsing)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("androidx.compose.animation:animation:1.6.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.core:core-splashscreen:1.0.0")
    debugImplementation(libs.androidx.ui.tooling)

    val lifecycleVersion = "2.11.0" // Use the latest stable version

    // Core ViewModel library for standard or Multiplatform logic
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // REQUIRED if you are using Jetpack Compose (provides the viewModel() composable function)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    // OPTIONAL extensions for Activities/Fragments (provides the 'by viewModels()' delegate)
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.0")

    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")

}