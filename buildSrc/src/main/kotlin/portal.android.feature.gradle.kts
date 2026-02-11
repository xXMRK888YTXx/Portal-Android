
plugins {
   id("portal.android.base")
   id("com.google.devtools.ksp")
   id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":CoreAndroid"))
    ksp(libs.dagger.compiler)
}
