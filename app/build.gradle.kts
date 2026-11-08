plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.atulyamvideocreatorapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.atulyamvideocreatorapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)


    val media3Version = "1.5.1" // or latest stable version
    implementation("androidx.media3:media3-transformer:$media3Version")
    implementation("androidx.media3:media3-effect:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation(libs.androidx.compose.material.icons.core)

    // OPTIONAL: Add this ONLY if you need the full, massive list of Material icons
    // Warning: This can significantly increase your app's compilation time and APK size
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.coil.compose)

    // Required for viewModels() delegate and Compose extensions
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.media3.datasource)
    // The working, modern replacement for ffmpeg-kit-full on Maven Central
    //implementation(libs.ffmpegkit.maintained.ffmpeg.kit.full)
    implementation(libs.ffmpeg.kit.full.v817)

    // REQUIRED: FFmpegKit has a mandatory dependency on smart-exception to parse stack traces

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}