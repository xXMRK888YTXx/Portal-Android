import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.oss.licenses.plugin)
}
val catalogJavaVersion = libs.versions.jvm.target.get()
val catalogCompileSdk = libs.versions.compile.sdk.get().toInt()
val catalogMinSdk = libs.versions.min.sdk.get().toInt()
val catalogTargetSdk = libs.versions.target.sdk.get().toInt()


extensions.configure<ApplicationExtension> {
    namespace = "com.xxmrk888ytxx.portal"
    compileSdk {
        version = release(catalogCompileSdk)
    }

    defaultConfig {
        applicationId = "com.xxmrk888ytxx.portal"
        minSdk = catalogMinSdk
        targetSdk = catalogTargetSdk
        versionCode = 43
        versionName = "Eve.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
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
    implementation(projects.coreCompose)
    implementation(projects.onboardingScreen)
    implementation(projects.preferencesStorage)
    implementation(projects.mainScreen)
    implementation(projects.addNewDeviceScreen)
    implementation(projects.database)
    implementation(projects.deviceConfigurationScreen)
    implementation(projects.unlockService)
    implementation(projects.biometricAuthentication.compose)
    implementation(projects.logsScreen)
    implementation(projects.settingsScreen)

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
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.code.scanner)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
