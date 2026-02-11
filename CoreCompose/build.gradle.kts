plugins {
    id("portal.android.base")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.xxmrk888ytxx.corecompose"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":CoreAndroid"))
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.graphics)
    api(libs.androidx.compose.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.lifecycle.compose)
    api(libs.androidx.compose.tooling)
    api(libs.androidx.compose.ui.text.google.fonts)
    api(libs.lifecycle.viewModel.compose)

    debugApi(libs.androidx.compose.tooling)
}