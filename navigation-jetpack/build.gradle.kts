import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(21)
    jvm()
    
    androidLibrary {
        namespace = "com.mrboomdev.navigation.jetpack"
        compileSdk = properties["android.targetSdk"].toString().toInt()
        minSdk = properties["android.minSdk"].toString().toInt()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(projects.navigationCore)
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.navigation)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}