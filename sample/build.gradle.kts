import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":navigation-core"))
                implementation(project(":navigation-jetpack"))
                
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.ui)
                implementation(libs.compose.material3)
                implementation(libs.compose.navigation)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.material)
                implementation(libs.androidx.activity.compose)
            }
        }
    }
}

android {
    namespace = "com.mrboomdev.navigation.sample"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        aidl = false
        buildConfig = false
        dataBinding = false
        mlModelBinding = false
        prefab = false
        renderScript = false
        resValues = false
        shaders = false
        viewBinding = false
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}