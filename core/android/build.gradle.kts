import com.android.build.api.dsl.LibraryExtension

plugins {
    id("portal.android.base")
}

extensions.configure<LibraryExtension> {
    namespace = "com.xxmrk888ytxx.coreandroid"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)

    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.activity.compose)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.dagger.core)
    api(libs.coroutines.android)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlin.serialization.core)
}
