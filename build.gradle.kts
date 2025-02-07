// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.gradle)
        classpath(libs.google.services)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false

}
