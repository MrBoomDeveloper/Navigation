plugins {
    alias(libs.plugins.maven.publish) apply false

    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library.multiplatform) apply false
}