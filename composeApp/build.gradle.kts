plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.room")
    id("org.jetbrains.kotlin.plugin.proc")
}

group = "com.rarcega"
version = "1.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Navigation
                implementation("cafe.adriel.voyager:voyager-navigator:1.1.0")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:1.1.0")
                implementation("cafe.adriel.voyager:voyager-transitions:1.1.0")

                // Networking
                implementation("io.ktor:ktor-client-core:3.0.3")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
                implementation("io.ktor:ktor-client-logging:3.0.3")

                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

                // Room
                implementation("androidx.room:room-common:2.7.0-alpha12")

                // Koin
                implementation("io.insert-koin:koin-core:3.5.6")
                implementation("io.insert-koin:koin-compose:3.5.6")

                // Charts
                implementation("io.github.bytebeats:compose-charts:0.1.1")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.9.3")
                implementation("io.ktor:ktor-client-okhttp:3.0.3")
                implementation("androidx.room:room-runtime:2.7.0-alpha12")
                implementation("androidx.room:room-ktx:2.7.0-alpha12")
                implementation("io.insert-koin:koin-android:3.5.6")
                implementation("io.insert-koin:koin-androidx-compose:3.5.6")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation("io.ktor:ktor-client-java:3.0.3")
                implementation("androidx.room:room-runtime:2.7.0-alpha12")
                implementation("androidx.room:room-sqlite:2.7.0-alpha12")
                implementation("io.insert-koin:koin-core:3.5.6")
                implementation("io.insert-koin:koin-compose:3.5.6")
            }
        }

        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.3")
                implementation("androidx.room:room-runtime:2.7.0-alpha12")
                implementation("androidx.room:room-sqlite:2.7.0-alpha12")
                implementation("io.insert-koin:koin-core:3.5.6")
                implementation("io.insert-koin:koin-compose:3.5.6")
            }
        }
    }
}

android {
    namespace = "com.rarcega.controlgastos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rarcega.controlgastos"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

compose.desktop {
    application {
        mainClass = "com.rarcega.controlgastos.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "ControlGastosApp"
            packageVersion = "1.0.0"
            description = "Aplicación de control de gastos"
            copyright = "© 2026 Rarcega"

            windows {
                menuGroup = "ControlGastos"
                perUserInstall = true
                dirChooser = true
            }
        }
    }
}
