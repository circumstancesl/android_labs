// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("androidx.room") version "2.6.1" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    kotlin("plugin.serialization") version "2.0.21"
}

buildscript {
    dependencies {
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}