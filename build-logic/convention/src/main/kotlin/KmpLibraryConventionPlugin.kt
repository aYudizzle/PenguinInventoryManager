import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

class KmpLibraryConventionPlugin : Plugin<Project> {
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

            // Wende nur die absolut notwendigen KMP- und Android-Plugins an
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply("com.android.kotlin.multiplatform.library")
            plugins.apply("com.android.lint")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")

            val kotlin = extensions.getByType<KotlinMultiplatformExtension>()

            // Konfiguriere die KMP-Targets (Android + Desktop)
            kotlin.apply {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlin.uuid.ExperimentalUuidApi",
                        "-opt-in=kotlin.time.ExperimentalTime",
                        "-opt-in=kotlinx.datetime.ExperimentalDatetimeApi"
                    )
                }
                androidLibrary {
                    minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
                    compileSdk = libs.findVersion("android-targetSdk").get().toString().toInt()

                    lint {
                        disable.add("RestrictedApi")
                    }
                }
                jvm() // Desktop-Target
            }

            // Konfiguriere die SourceSets
            kotlin.sourceSets.apply {

                getByName("commonMain").dependencies {
                    // Füge hier nur absolut notwendige Libs hinzu
                    // z.B. Coroutinen und Koin-Core (um Module zu definieren)

                    // implementation(libs.findLibrary("kotlinx-coroutines-core").get()) // Beispiel
                    implementation(libs.findLibrary("koin-core").get())
                    implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                }

                getByName("commonTest").dependencies {
                    implementation(libs.findLibrary("kotlin-test").get())
                }

                getByName("androidMain").dependencies {

                }

                getByName("jvmMain").dependencies {
                    // Bleibt leer, außer du brauchst JVM-spezifische APIs
                }
            }
        }
    }
}