plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.safeArgs)
}

android {
    namespace = "com.monsalud.fivedayforecaster"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.monsalud.fivedayforecaster"
        minSdk = 24
        targetSdk = 34
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

//    kapt {
//        useBuildCache = false
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}

dependencies {

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Architecture & Lifecycle
    implementation(libs.androidx.activity)

    // Kotlin

    // Google
    implementation(libs.material)
    implementation(libs.google.gson)

    // UI
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)
    implementation(libs.androidx.library)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.picasso)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Ktor
    implementation(libs.ktor.core)
    implementation(libs.ktor.android)
    implementation(libs.ktor.serialization)

    // Testing
    implementation(libs.androidx.room.testing)
    implementation(libs.koin.test)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.arch.core.test)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.koin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    // Instrumented Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
}