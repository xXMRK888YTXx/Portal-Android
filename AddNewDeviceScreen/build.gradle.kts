import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.portal.android.compose.feature)
}

extensions.configure<LibraryExtension> {
    namespace = "com.xxmrk888ytxx.addnewdevicescreen"
}