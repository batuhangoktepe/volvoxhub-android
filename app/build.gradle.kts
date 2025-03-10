plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    id("dagger.hilt.android.plugin")
    id("maven-publish")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.firebase.crashlytics")
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "com.hub.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hub.example"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Volvoxhub
    implementation(project(":volvoxhub"))

    // DI
    kapt(libs.hilt.compiler)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt)

    // Gson
    implementation(libs.gson)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}

kapt {
    correctErrorTypes = true
}
