plugins {
    alias(libs.plugins.ayupi.kmp.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "dev.ayupi.pim.core.ui"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)

            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation(libs.kotlinx.datetime)
        }
    }
}