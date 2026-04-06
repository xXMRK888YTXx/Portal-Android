import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("portal.android.base")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<LibraryExtension> {
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
    implementation(libs.material.kolor)


    debugApi(libs.androidx.compose.tooling)
}