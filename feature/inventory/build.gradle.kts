plugins {
    alias(libs.plugins.ayupi.kmp.feature)
}

kotlin {
    androidLibrary {
        namespace = "dev.ayupi.pse_new.feature.inventory"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.ui)
            implementation(projects.core.data)
            implementation(projects.core.model)
        }
    }
}