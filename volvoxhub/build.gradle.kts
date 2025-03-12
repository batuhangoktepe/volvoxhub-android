plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    `maven-publish`
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "com.volvoxmobile.volvoxhub"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        version = 1
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
}

dependencies {

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Network
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.retrofitConverterGson)

    // Kotlin Result
    implementation(libs.kotlinResult)

    // Ads Identifier
    implementation(libs.adsIdentifier)

    // Purchases
    implementation(libs.revenuecat)
    implementation(libs.revenuecatui)

    // Facebook
    implementation(libs.facebookAndroidSdk)

    // OneSignal
    implementation(libs.oneSignal)

    // Appsflyer Sdk
    implementation(libs.appsflyer)

    // Local DB
    implementation(libs.room)
    implementation(libs.roomKtx)
    kapt(libs.roomCompiler)

    // Fuel
    implementation(libs.fuel)
    implementation(libs.fuelCorutines)
    implementation(libs.fuelGson)
    implementation(libs.fuelAndroid)

    // Root Detection
    implementation(libs.rootbeer)

    // Amplitude
    implementation(libs.amplitude)
    implementation(libs.amplitudeExperiment)

    // Firebase
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation (libs.hilt)
    kapt (libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation)

    // Coil
    implementation(libs.coil)
}

kapt {
    correctErrorTypes = true
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.volvoxmobile.volvoxhub"
                artifactId = "volvoxhub"
                version = "0.0.4"
            }
        }
    }
}
