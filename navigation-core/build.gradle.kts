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

mavenPublishing {
    pom {
        name = "Navigation by MrBoomDev"
        description = "A simple navigation library for Compose Multiplatform projects."
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