plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting{
            dependencies{
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)

                implementation("com.squareup.sqldelight:runtime:1.5.5")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.5.5")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                api("dev.icerock.moko:mvvm-core:0.16.1") // only ViewModel, EventsDispatcher, Dispatchers.UI
                api("dev.icerock.moko:mvvm-compose:0.16.1") // api mvvm-core, getViewModel for Compose Multiplatfrom
                api("dev.icerock.moko:mvvm-flow-compose:0.16.1") // api mvvm-flow, binding extensions for Compose Multiplatform
                api("dev.icerock.moko:mvvm-flow:0.16.1") // api mvvm-core, CFlow for native and binding extensions

                implementation("co.touchlab:kermit:2.0.2")//kermit for logging

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

                //precompose navigation
                val precompose_version = "1.5.7"
                api("moe.tlaster:precompose:$precompose_version")

                //okio for file directory access
                implementation("com.squareup.okio:okio:3.6.0")
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.androidx.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)

                implementation("com.squareup.sqldelight:android-driver:1.5.5")

                // Exoplayer
                implementation ("com.google.android.exoplayer:exoplayer-core:2.18.1")

                // Audio visualizers
                implementation ("com.github.Armen101:AudioRecordView:1.0.4")
                implementation ("com.github.massoudss:waveformSeekBar:5.0.1")
                implementation ("com.github.lincollincol:amplituda:2.1.6")
            }
        }

        val iosMain by creating {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.5")
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

sqldelight {
    database("JournyPrototypeDatatbase"){
        packageName = "com.prototype.newkmm.database"
        sourceFolders = listOf("sqldelight")
    }
}

android {
    namespace = "com.prototype.newkmm"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
