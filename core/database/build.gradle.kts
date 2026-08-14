import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.portal.android.feature)
    alias(libs.plugins.room)
}

extensions.configure<LibraryExtension>  {
    namespace = "com.xxmrk888ytxx.database"
}
room3 { schemaDirectory("$projectDir/schemas") }


dependencies {
    api(libs.room.runtime)
    ksp(libs.room.compiler)
}