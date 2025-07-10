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
                implementation(libs.compose.navigation)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}