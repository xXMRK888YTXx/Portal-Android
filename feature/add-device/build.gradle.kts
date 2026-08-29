import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.portal.android.compose.feature)
}

extensions.configure<LibraryExtension> {
    namespace = "com.xxmrk888ytxx.addnewdevicescreen"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}