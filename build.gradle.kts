// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    //alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false // ✅ Add this if using Room or KSP-based processors
    //alias(libs.plugins.navigation.safe.args) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // ✅ Add JitPack for missing libs like code-scanner
    }
}
