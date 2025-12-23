pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}
buildscript {
  repositories {
    google()
    mavenCentral()
  }
  dependencies {
//    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
    classpath("com.android.tools.build:gradle:8.3.1")
//    classpath("androidx.compose.compiler:compiler")
  }
}
