plugins {
    alias(libs.plugins.portal.android.compose.feature)
}

android {
    namespace = "com.xxmrk888ytxx.biometricauthentication.compose"
}

dependencies {
    api(projects.biometricAuthentication)
}