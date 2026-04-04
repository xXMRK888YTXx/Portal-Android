plugins {
    `kotlin-dsl`
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    //implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.compiler.plugin)
    implementation(libs.ksp)
    implementation(libs.kotlin.serialization.plugin)
    implementation(libs.oss.licenses.plugin)
    // Make version catalog available in precompiled scripts
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
