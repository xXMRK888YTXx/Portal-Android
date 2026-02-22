import com.android.build.api.dsl.ApplicationExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}
val catalogJavaVersion = libs.versions.jvm.target.get()
val catalogCompileSdk = libs.versions.compile.sdk.get().toInt()
val catalogMinSdk = libs.versions.min.sdk.get().toInt()


extensions.configure<ApplicationExtension> {
    namespace = "com.xxmrk888ytxx.portal"
    compileSdk {
        version = release(catalogCompileSdk)
    }

    defaultConfig {
        applicationId = "com.xxmrk888ytxx.portal"
        minSdk = catalogMinSdk
        targetSdk = catalogCompileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":CoreCompose"))
    implementation(project(":OnboardingScreen"))
    implementation(project(":PreferencesStorage"))
    implementation(project(":MainScreen"))
    implementation(project(":AddNewDeviceScreen"))
    implementation(project(":Database"))
    implementation(project(":DeviceConfigurationScreen"))

    ksp(libs.dagger.compiler)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.androidx.splashScreen)
    implementation(libs.bouncycastle.bcpkix.jdk18on)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.slf4j.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}