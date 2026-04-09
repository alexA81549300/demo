plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
  namespace = "ai.hermes.oracle"
  compileSdk = 34
  defaultConfig {
    applicationId = "ai.hermes.oracle"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "0.1"
  }
  buildTypes { release { isMinifyEnabled = false } }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Shizuku SDK
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
  implementation("org.nanohttpd:nanohttpd:2.3.1")
}
