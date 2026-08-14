import com.android.build.api.dsl.LibraryExtension

plugins {
    id("portal.android.feature")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<LibraryExtension> {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:compose"))
}