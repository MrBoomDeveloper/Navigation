plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvm()

    androidLibrary {
        namespace = "com.mrboomdev.navigation.deeplinks"
        compileSdk = 35
        minSdk = 24
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}