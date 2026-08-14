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
        versionCode = 46
        versionName = catalogVersionName

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
    packaging {
        resources {
            merges += "/META-INF/LICENSE.md"
            merges += "/META-INF/NOTICE.md"
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.feature.onboarding)
    implementation(projects.common.preferencesStorage)
    implementation(projects.feature.main)
    implementation(projects.feature.addDevice)
    implementation(projects.core.database)
    implementation(projects.feature.deviceConfiguration)
    implementation(projects.core.unlockService)
    implementation(projects.common.biometric.compose)
    implementation(projects.feature.logs)
    implementation(projects.feature.settings)

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
    implementation(libs.play.services.wearable)
    implementation(libs.kotlin.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
