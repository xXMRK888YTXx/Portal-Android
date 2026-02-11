plugins {
    id("portal.android.feature")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":CoreCompose"))
}