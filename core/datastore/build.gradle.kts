plugins {
    alias(libs.plugins.ayupi.kmp.library)
}

kotlin {
    androidLibrary {
        namespace = "dev.ayupi.pse_new.core.datastore"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.datastore.core.okio)

            implementation(libs.squareup.okio)

        }
    }
}