import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.api.attributes.Attribute
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configurations.configureEach {
                if (name.startsWith("jvm")) {
                    attributes {
                        attribute(
                            KotlinPlatformType.attribute,
                            KotlinPlatformType.jvm
                        )
                    }
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply("com.android.kotlin.multiplatform.library") // Nutzt dein alias
            plugins.apply("com.android.lint")
            plugins.apply("org.jetbrains.compose")
            plugins.apply("org.jetbrains.kotlin.plugin.compose")
            plugins.apply("org.jetbrains.compose.hot-reload")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")

            val kotlin = extensions.getByType<KotlinMultiplatformExtension>()
            val compose = extensions.getByType<ComposeExtension>()

            kotlin.apply {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.uuid.ExperimentalUuidApi",      // Für UUID
                        "-opt-in=kotlin.time.ExperimentalTime",         // Falls du explizit danach gefragt wirst
                        "-opt-in=kotlinx.datetime.ExperimentalDatetimeApi" // Für spezielle Datetime-Formatter
                    )
                }
                androidLibrary {
                    // SDK-Versionen werden zentral aus der toml geholt
                    minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
                    compileSdk = libs.findVersion("android-targetSdk").get().toString().toInt()

                    withDeviceTestBuilder {
                        sourceSetTreeName = "test"
                    }.configure {
                        instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    }
                }

                jvm() // Desktop-Target
            }

            kotlin.sourceSets.apply {
                // Common - Shared
                getByName("commonMain").dependencies {
                    val composeDeps = compose.dependencies
                    implementation(composeDeps.runtime)
                    implementation(composeDeps.foundation)
                    implementation(composeDeps.material3)
                    implementation(composeDeps.ui)
                    implementation(composeDeps.components.resources)
                    implementation(composeDeps.components.uiToolingPreview)
                    implementation(composeDeps.materialIconsExtended)

                    implementation(libs.findLibrary("kotlinx-serialization-json").get())
                    implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                    implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())

                    implementation(libs.findLibrary("koin-compose-viewmodel-nav").get())

                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                }

                getByName("commonTest").dependencies {
                    implementation(libs.findLibrary("kotlin-test").get())
                }
                // Android
                getByName("androidMain").dependencies {
                    val composeDeps = compose.dependencies
                    implementation(composeDeps.preview)
                    implementation(libs.findLibrary("androidx-activity-compose").get())
                    implementation(libs.findLibrary("kotlinx-coroutines-android").get())
                }
                // Desktop
                getByName("jvmMain").dependencies {
                    val composeDeps = compose.dependencies
                    implementation(composeDeps.desktop.currentOs)
                    implementation(libs.findLibrary("kotlinx-coroutinesSwing").get())
                }
            }

        }
    }
}