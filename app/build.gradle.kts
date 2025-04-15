plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    alias(libs.plugins.kotlin.serialization)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.blescanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.blescanner"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation(libs.kotlinStdlib)

    implementation(libs.bundles.composeCore)
    implementation(libs.appcompat)
    debugImplementation("androidx.compose.ui:ui-tooling:${libs.versions.compose.get()}")

    implementation(libs.bundles.networking)
    implementation(libs.bundles.coroutines)

    implementation(libs.bundles.hilt)
    kapt(libs.hiltCompiler)

    implementation(libs.coilCompose)

    implementation(libs.bundles.room)
    kapt(libs.roomCompiler)

    implementation(libs.lifecycle.service)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.timber)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
}
