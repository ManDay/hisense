plugins {
  id("com.android.application") version "8.13.2"
  id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
  id("org.jetbrains.kotlin.android") version "2.2.0"
  id("org.jetbrains.kotlin.kapt") version "2.2.0"
}

kotlin {
 jvmToolchain(17)
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
    externalNativeBuild {
        cmake {
            path = file( "src/main/cpp/CMakeLists.txt" )
            buildStagingDirectory = file( "outputs/cmake" )
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui:1.10.0")
    implementation("androidx.compose.ui:ui-graphics:1.10.0")
    implementation("androidx.compose.ui:ui-tooling:1.10.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.35.0-alpha")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("androidx.room:room-ktx:2.8.4")
    kapt( "androidx.room:room-compiler:2.8.4" )
}
