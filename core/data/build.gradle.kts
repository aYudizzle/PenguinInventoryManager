plugins {
    alias(libs.plugins.ayupi.kmp.library)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "dev.ayupi.pse_new.core.data"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)

            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.network)

            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.konnectivity)
        }
    }
}