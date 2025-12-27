import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(21)
    jvm()

    androidLibrary {
        namespace = "com.mrboomdev.navigation.jetpack3"
        compileSdk = 35
        minSdk = 24

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
                api(project(":navigation-core"))
                implementation(libs.kotlin.stdlib)
                implementation(libs.compose.navigation3.runtime)
                implementation(libs.compose.navigation3.ui)
                implementation(libs.compose.navigation3.viewmodel)
            }
        }

//        commonTest {
//            dependencies {
//                implementation(libs.kotlin.test)
//            }
//        }
    }
}