plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "org.versaillesumc.firetv"
    compileSdk = 35
    defaultConfig { applicationId = "org.versaillesumc.firetv"; minSdk = 23; targetSdk = 28; versionCode = 1; versionName = "0.1.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
}
