pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Navigation"
include(":navigation-core")
include(":navigation-jetpack")
//include(":navigation-jetpack3")
include(":deeplinks")
include(":sample")