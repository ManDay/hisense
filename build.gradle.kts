plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
    compileSdkVersion(34)

    defaultConfig {
        applicationId = "com.hisense.einkservice"
        minSdkVersion(30)
        targetSdkVersion(34)
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
    implementation("androidx.core.ktx")
    implementation("androidx.lifecycle.runtime.ktx")
    implementation("androidx.activity.compose")
    implementation("androidx.ui")
    implementation("androidx.ui.graphics")
    implementation("androidx.ui.tooling")
    implementation("androidx.material3")
    implementation("androidx.lifecycle.viewmodel.compose")
    implementation("androidx.room.runtime")
    implementation("accompanist.swiperefresh")
    implementation("androidx.navigation.compose")
    implementation("androidx.room.ktx")
}
