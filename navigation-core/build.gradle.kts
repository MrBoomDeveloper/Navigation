plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvm()
    
    androidLibrary {
        namespace = "com.mrboomdev.navigation.core"
        compileSdk = 35
        minSdk = 24
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.kotlinx.serialization.json)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}