import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
    alias(libs.plugins.compose.compiler)
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}