plugins {
    alias(libs.plugins.portal.android.compose.feature)
}

android {
    namespace = "com.xxmrk888ytxx.settingsscreen"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.okhttp)
    implementation(libs.androidx.ui)
    implementation(libs.play.services.oss.licenses)
}