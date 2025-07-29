plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.todokanai.musicplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.todokanai.musicplayer"
        minSdk = 30
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"

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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
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

    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation (platform(libs.androidx.compose.bom))

    implementation (libs.androidx.core.ktx)
    implementation (libs.androidx.lifecycle.common)
    implementation (libs.androidx.activity.compose)

    implementation (libs.androidx.lifecycle.runtime.compose.android)
    implementation (libs.androidx.constraintlayout)     // enable ConstraintLayout
    implementation (libs.material)
    implementation (libs.androidx.fragment.ktx)

    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

    implementation (libs.androidx.legacy.support.v4)

    debugImplementation (libs.androidx.ui.tooling)      // compose preview
    implementation (libs.androidx.ui)
    implementation (libs.androidx.material3)

    implementation (libs.picasso)
    implementation (libs.androidx.hilt.navigation.compose)

    implementation (libs.coil.compose)

    implementation (libs.androidx.constraintlayout.compose)     // ConstraintLayout for compose
    implementation (libs.androidx.appcompat)

    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.hilt.android)
    kapt (libs.hilt.android.compiler)
    implementation (libs.androidx.datastore.preferences)       // enable DataStore
}