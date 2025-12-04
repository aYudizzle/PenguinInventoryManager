plugins {
    alias(libs.plugins.ayupi.kmp.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}
val projectDir = project.layout.projectDirectory

kotlin {
    lint {
        // Room KMP generiert Code, der interne APIs nutzt.
        // Das ist okay, aber Lint meckert. Wir schalten es stumm.
        warning.add("RestrictedApi")
        // Alternativ, falls warning nicht reicht:
        // disable.add("RestrictedApi")
    }

    androidLibrary {
        namespace = "dev.ayupi.pse_new.core.database"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.datetime)

            implementation(libs.kotlinx.serialization.json)

        }
        androidMain.dependencies {
            implementation(libs.androidx.room.sqlite.wrapper)
        }
    }
}
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}
room {
    schemaDirectory("$projectDir/schemas")
}