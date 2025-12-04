import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.ayupi.kmp.library)
    alias(libs.plugins.buildKonfig)
}



buildkonfig {
    packageName = "dev.ayupi.pse_new.core.network"

    val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("API_KEY", "")
    val baseUrl: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL", "localhost")

    defaultConfigs {
        buildConfigField(STRING, "API_BASE_URL", baseUrl)
        buildConfigField(STRING, "API_KEY", apiKey)
    }
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "dev.ayupi.pse_new.core.network"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.datetime)

        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
    }
}