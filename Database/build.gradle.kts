import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.portal.android.feature)
    alias(libs.plugins.room)
}

extensions.configure<LibraryExtension>  {
    namespace = "com.xxmrk888ytxx.database"
}
room { schemaDirectory("$projectDir/schemas") }


dependencies {
    api(libs.room.ktx)
    api(libs.room.runtime)

    ksp(libs.room.compiler)
}