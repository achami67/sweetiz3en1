plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ Nécessaire pour Firebase
}

android {
    namespace = "com.example.production"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.production"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }

    configurations.all {
        resolutionStrategy {
            force("androidx.appcompat:appcompat:1.6.1")
            force("androidx.activity:activity:1.8.2")
            force("androidx.constraintlayout:constraintlayout:2.1.4")
            force("com.google.android.material:material:1.11.0")
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 🔥 Firebase BoM (recommandé)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // ✅ Firebase Realtime Database (version gérée automatiquement par BoM)
    implementation("com.google.firebase:firebase-database")

    // 🔧 Gson pour le JSON
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
