plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val catalogJavaVersion = libs.versions.jvm.target.get()
val catalogCompileSdk = libs.versions.compile.sdk.get().toInt()
val catalogMinSdk = libs.versions.min.sdk.get().toInt()
val catalogTargetSdk = libs.versions.target.sdk.get().toInt()
val catalogVersionName = libs.versions.version.name.get()
val catalogApplicationId = libs.versions.application.id.get()

android {
    namespace = catalogApplicationId
    compileSdk {
        version = release(catalogCompileSdk)
    }

    defaultConfig {
        applicationId = catalogApplicationId
        minSdk = catalogMinSdk
        targetSdk = catalogTargetSdk
        versionCode = 1
        versionName = catalogVersionName

    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$catalogJavaVersion")
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.coreCompose)
    implementation(projects.preferencesStorage)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.splashScreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewModel.compose)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.compose.ui.tooling)
    implementation(libs.play.services.wearable)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //Dagger
    ksp(libs.dagger.compiler)
}
