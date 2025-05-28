import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library.multiplatform)
    alias(libs.plugins.maven.publish)
}

group = "ru.mrboomdev.navigation"
version = property("library.version")!!

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

mavenPublishing {
    pom {
        name = "Navigation Jetpack by MrBoomDev"
        description = "An implementation of the library made by using Jetpack Navigation made by Jetbrains."
        url = "https://github.com/MrBoomDeveloper/Navigation"
        inceptionYear = "2025"

        licenses {
            license {
                name = "The Apache Licence, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                description = url
            }
        }

        developers {
            developer {
                id = "mrboomdev"
                name = "MrBoomDev"
                url = "https://github.com/MrBoomDeveloper"
            }
        }

        scm {
            url = "https://github.com/MrBoomDeveloper/Navigation"
            connection = "scm:git:git://github.com/MrBoomDeveloper/Navigation.git"
            developerConnection = "scm:git:ssh://git@github.com/MrBoomDeveloper/Navigation.git"
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, false)
    signAllPublications()
}