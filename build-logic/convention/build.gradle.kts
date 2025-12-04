//@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    `kotlin-dsl`
}

group = "dev.ayupi.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
dependencies {
        compileOnly(libs.kotlin.gradlePlugin)
        compileOnly(libs.android.gradlePlugin)
        compileOnly(libs.composeMultiplatform.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpFeature") {
            id = "ayupi.kmp.feature"
            implementationClass = "KmpFeatureConventionPlugin"
        }
        register("kmpLibrary") {
            id = "ayupi.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }
    }
//    plugins {
//        // Registriere deine neuen KMP-Plugins
//        register("kmpFeature") {
//            id = "ayupi.kmp.feature"
//            implementationClass = "KmpFeatureConventionPlugin"
//        }
//        register("kmpLibrary") {
//            id = "ayupi.kmp.library"
//            implementationClass = "KmpLibraryConventionPlugin"
//        }
//    }
}