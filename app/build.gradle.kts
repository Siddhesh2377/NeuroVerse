import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.dark.neuroverse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dark.neurov"
        minSdk = 33
        targetSdk = 36
        versionCode = 3
        versionName = "0.3-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val localPropertiesFile = rootProject.file("local.properties")
        val apiKey = if (localPropertiesFile.exists()) {
            val localProps = Properties().apply {
                load(FileInputStream(localPropertiesFile))
            }
            localProps.getProperty("API_KEY") ?: "sample_dev_key"
        } else {
            System.getenv("API_KEY") ?: "sample_dev_key"
        }

        buildConfigField("String", "API_KEY", apiKey)
    }
    buildTypes {
        release {
            isMinifyEnabled = true           // Enable code shrinking
            isShrinkResources = true         // Remove unused resources
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    //PROJECTS
    implementation(project(":ai-manager"))
    implementation(project(":plugin-api"))
    implementation(project(":plugin-runtime"))
   // implementation(project(":smollm"))

    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    //DATABASE
    implementation(libs.androidx.room.runtime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    // Navigation Compose (NOT Multiplatform)
    implementation("androidx.navigation:navigation-compose:2.9.0")
    // Accompanist Navigation Animation
    implementation("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler)

    //UTILS
    implementation(libs.google.gson)
    implementation(libs.androidx.datastore.preferences)

    //API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)

    //KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //CORE-UI-LIBS
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.animation:animation:1.8.2")

    //TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    //DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}