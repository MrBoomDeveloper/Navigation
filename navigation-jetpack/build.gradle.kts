plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()
    
    androidLibrary {
        namespace = "com.mrboomdev.navigation.jetpack"
        compileSdk = 35
        minSdk = 24
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(project(":navigation-core"))
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                api(libs.compose.navigation)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}