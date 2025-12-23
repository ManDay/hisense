plugins {
  id("com.android.application:8.3.1")
}

android {
    namespace = "com.hisense.einkservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hisense.einkservice"
        minSdk = 30
        targetSdk = 34
        versionCode = 7
        versionName = "1.6"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildFeatures {
        compose = true
        aidl = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation("androidx.core.ktx:1.13.0")
    implementation("androidx.lifecycle.runtime.ktx:2.7.0")
    implementation("androidx.activity.compose:1.9.0")
    implementation("androidx.ui:1.6.6")
    implementation("androidx.ui.graphics:1.6.6")
    implementation("androidx.ui.tooling:1.6.6")
    implementation("androidx.material3:2024.04.01")
    implementation("androidx.lifecycle.viewmodel.compose:2.7.0")
    implementation("androidx.room.runtime:2.6.1")
    implementation("accompanist.swiperefresh:0.35.0-alpha")
    implementation("androidx.navigation.compose:2.7.0")
    implementation("androidx.room.ktx:2.6.1")
}
