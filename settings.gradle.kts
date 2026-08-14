enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Portal"
include(":app")

include(":feature")
include(":feature:onboarding")
include(":feature:main")
include(":feature:add-device")
include(":feature:device-configuration")
include(":feature:logs")
include(":feature:settings")


include(":core")
include(":core:android")
include(":core:compose")
include(":core:database")
include(":core:unlock-service")

include(":common")
include(":common:preferences-storage")
include(":common:biometric")
include(":common:biometric:compose")

include(":wearApp")
