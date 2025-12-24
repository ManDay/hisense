plugins {
  id("com.android.application") version "8.13.2"
}

android {
    namespace = "com.hisense.einkservice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hisense.einkservice"
        minSdk = 30
        targetSdk = 35
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
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.12.2")
    implementation("androidx.compose.ui:ui:1.10.0")
    implementation("androidx.compose.ui:ui-graphics:1.10.0")
    implementation("androidx.compose.ui:ui-tooling:1.10.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.35.0-alpha")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.room:room-ktx:2.8.4")
}
