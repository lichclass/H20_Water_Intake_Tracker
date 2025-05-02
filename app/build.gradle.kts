import java.util.Properties

val localProperties = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { localProperties.load(it) }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.shysoftware.h20tracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shysoftware.h20tracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_AUTH_URL", "\"${localProperties["SUPABASE_AUTH_URL"]}\"")
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties["SUPABASE_URL"]}\"")
        buildConfigField("String", "SUPABASE_API_KEY", "\"${localProperties["SUPABASE_API_KEY"]}\"")
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProperties["OPENWEATHER_API_KEY"]}\"")
        buildConfigField("String", "MAPBOX_TOKEN", "\"${localProperties["MAPBOX_TOKEN"]}\"")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // HTTP Client (Handles APIs)
    implementation(libs.okhttp)

    // ViewModel
    implementation(libs.lifecycle.viewmodel)
    // LiveData
    implementation(libs.lifecycle.livedata)
    // Runtime
    implementation(libs.lifecycle.runtime)

    // Gson
    implementation(libs.gson)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}