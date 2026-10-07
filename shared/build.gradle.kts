plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("com.android.library") version "8.5.2" apply false
}

kotlin {
    jvm("desktop")
    androidTarget()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        getByName("desktopMain").dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.vlcj)
        }
    }
}
