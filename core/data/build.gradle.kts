plugins {
    alias(libs.plugins.ayupi.kmp.library)
}

kotlin {

    androidLibrary {
        namespace = "dev.ayupi.pim.core.data"
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