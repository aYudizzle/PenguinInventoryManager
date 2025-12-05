import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.navigation)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.compose.viewmodel.nav)

            implementation(projects.feature.storageDetails)
            implementation(projects.feature.storageOverview)
            implementation(projects.feature.itementry)
            implementation(projects.feature.settings)
            implementation(projects.feature.itemmaster)
            implementation(projects.feature.inventory)

            implementation(projects.core.data)
            implementation(projects.core.model)
            implementation(projects.core.ui)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(compose.components.resources)
        }
    }
}

android {
    namespace = "dev.ayupi.pim"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.ayupi.pim"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "dev.ayupi.pim.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Penguin Inventory Manager"
            packageVersion = "1.0.0"

            description = "Die smarte Offline-First Lagerverwaltung"
            vendor = "ayupi.dev"

            macOS {
                iconFile.set(project.file("src/jvmMain/resources/launcher/pimlogo.icns"))
                bundleID = "dev.ayupi.pim"
                dockName = "PIM"
            }

            windows {
                upgradeUuid = "44f3262b-bbf5-47e6-948d-2bd1eecdbf74"
                menuGroup = "ayupi software"
                shortcut = true
                iconFile.set(project.file("src/jvmMain/resources/launcher/pimlogo.ico"))
            }

            linux {
                packageName = "penguin-inventory-manager"
                iconFile.set(project.file("src/jvmMain/resources/launcher/pimlogo.png"))
            }
        }
    }
}
