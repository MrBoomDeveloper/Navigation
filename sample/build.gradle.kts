import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":navigation-core"))
                implementation(project(":navigation-jetpack"))
                implementation(project(":deeplinks"))
                
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
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
        
        jvmMain.dependencies {
            // For some fucking reason skiko isn't loaded by default
            val osName = System.getProperty("os.name")
            val osArch = System.getProperty("os.arch")

            val targetOs = when {
                osName == "Mac OS X" -> "macos"
                osName.startsWith("Win") -> "windows"
                osName.startsWith("Linux") -> "linux"
                else -> throw UnsupportedOperationException("Unsupported platform $osName!")
            }

            val targetArch = when(osArch) {
                "x86_64", "amd64" -> "x64"
                "aarch64" -> "arm64"
                else -> throw UnsupportedOperationException("Unsupported cpu acrhitecture $osArch!")
            }

            runtimeOnly("org.jetbrains.skiko:skiko-awt-runtime-$targetOs-$targetArch:0.9.4.2")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "com.mrboomdev.navigation.sample.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Deb, TargetFormat.Msi)
            packageName = "com.mrboomdev.navigation.sample"
            packageVersion = "1.0.1"

            windows {
                console = true
                perUserInstall = true
                menu = true
                menuGroup = "Navigation Sample"
                includeAllModules = true
                upgradeUuid = "6a7f9795-c323-4c10-a73a-55ac5506af02"
            }
        }
    }
}