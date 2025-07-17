plugins {
    //id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safe.args)
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.parttracker"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.parttracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Room schema location argument (HERE!)
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        dataBinding = true
        compose = true
        viewBinding = true // Enable viewBinding for traditional layout-based activities
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // <--- Important
    }

    ksp {
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }




}

//dependencies {
//    implementation(libs.appcrawler.platform)
//    // Room (Local Database)
//    val roomVersion = "2.6.1"
//    implementation("androidx.room:room-runtime:$roomVersion")
//    implementation("androidx.room:room-ktx:$roomVersion")
//    ksp("androidx.room:room-compiler:$roomVersion")
//
//
//    // ZXing (QR Code Generator)
//    implementation("com.google.zxing:core:3.5.2")
//
//    // ML Kit Barcode Scanner (for QR Scanning)
//    implementation("com.google.mlkit:barcode-scanning:17.2.0")
//
//    // CameraX (Required by ML Kit)
//    val cameraxVersion = "1.3.0"
//    implementation("androidx.camera:camera-core:$cameraxVersion")
//    implementation("androidx.camera:camera-camera2:$cameraxVersion")
//    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
//    implementation("androidx.camera:camera-view:$cameraxVersion")
//    implementation("androidx.camera:camera-extensions:$cameraxVersion")
//
//    // ViewModel and LiveData
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
//
//    // Coroutines (optional but helpful)
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//
//    // AppCompat and UI
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.core:core-ktx:1.13.1")
//
//    // Optional: Jetpack Compose (only if you plan to migrate UI in future)
//    val composeVersion = "1.6.7"         // ✅ Latest stable for Kotlin 1.9.23
//    val material3Version = "1.2.1"       // ✅ Compatible with Compose 1.6.7
//
//    implementation("androidx.compose.ui:ui:$composeVersion")
//    implementation("androidx.compose.material3:material3:$material3Version")
//    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//    implementation("androidx.navigation:navigation-compose:2.7.7")
//
//    // Testing
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//
//    implementation("androidx.camera:camera-camera2:1.3.0");
//    implementation("androidx.camera:camera-lifecycle:1.3.0");
//    implementation("androidx.camera:camera-view:1.3.0");
//    implementation("com.google.mlkit:barcode-scanning:17.2.0")
//
//    implementation("com.google.android.material:material:1.12.0") // ✅ Add this line
//        // ... your other dependencies
//    implementation("androidx.compose.material3:material3:1.2.1")
//
//    implementation ("com.google.android.material:material:1.12.0")
//
//    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
//
//    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
//
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
//
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//
//// For viewModelScope support
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
//
//    implementation("androidx.appcompat:appcompat:1.6.1")
//
//    val nav_version = "2.7.7"
//    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
//    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
//
//    implementation("androidx.room:room-runtime:2.6.1")
//    ksp("androidx.room:room-compiler:2.6.1")  // if using KSP
//
//    implementation("androidx.room:room-ktx:2.6.1")
//
//    implementation ("com.google.android.material:material:1.12.0")
//
//
//
//
//
//
//
//
//}

dependencies {
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.firebase.firestore.ktx)
    // Room (Local Database)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // ZXing (QR Code Generator & Scanner)
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ML Kit Barcode Scanner
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX (Required by ML Kit)
    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Jetpack Compose (optional, future UI support)
    val composeVersion = "1.6.7"
    val material3Version = "1.2.1"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")

    // Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // UI & Material
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.mlkit:common:18.10.0") // ✅ Add this line

    implementation("com.google.android.material:material:1.12.0") // Or latest stable version
    implementation("androidx.compose.material3:material3:<latest_version>")

    implementation("androidx.recyclerview:recyclerview:1.3.1")

    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")


    // Firebase BoM - keeps versions in sync

// Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")

// Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")










}
