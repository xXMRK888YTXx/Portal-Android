import com.android.build.api.dsl.LibraryExtension
plugins {
    id("com.android.library")
}
val javaVersion = catalog.version("jvm-target")
extensions.configure<LibraryExtension> {
    // Android
    compileSdk = catalog.version("compile-sdk").toInt()
    defaultConfig {
        minSdk = catalog.version("min-sdk").toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Kotlin
    val javaVersion = catalog.version("jvm-target")
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    }
}