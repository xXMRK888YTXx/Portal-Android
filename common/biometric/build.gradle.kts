plugins {
    alias(libs.plugins.portal.android.feature)
}

android {
    namespace = "com.xxmrk888ytxx.biometricauthentication"
}

dependencies {
    implementation(libs.androidx.biometric)
}