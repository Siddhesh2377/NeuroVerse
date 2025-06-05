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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dark.neurov"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndkVersion = "29.0.13113456"
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

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }


    }

    packaging {
        resources.pickFirsts += listOf(
            "lib/**/libonnxruntime.so"
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas/app")
        }
    }

}

dependencies {

    //PROJECTS
    implementation(project(":ai-manager"))
    implementation(project(":plugin-api"))
    implementation(project(":plugin-runtime"))

    //DATABASE
    implementation(libs.androidx.room.runtime)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
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