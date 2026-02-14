plugins {
    alias(libs.plugins.portal.android.feature)
}

android {
    namespace = "com.xxmrk888ytxx.preferencesstorage"
}

dependencies {
    api(libs.datastore.preferences)
}