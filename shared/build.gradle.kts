import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("kotlin.time.ExperimentalTime")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.compose.ui.backhandler)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.okio)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.review.ktx)
        }
        // Data tests run on the JVM with Robolectric, like Mahjong Scoring 2 data tests
        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.robolectric)
            implementation(libs.androidx.test.core)
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.androidx.sqlite.framework)
            implementation(libs.androidx.compose.ui.test.junit4)
            implementation(libs.roborazzi)
        }
    }
}

android {
    namespace = "com.etologic.mahjongscoring3.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.etologic.mahjongscoring3.resources"
    generateResClass = always
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // ComponentActivity used by the Compose UI tests on Robolectric
    add("debugImplementation", libs.androidx.compose.ui.test.manifest)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

// MS2 golden files (shared/src/commonTest/resources/golden) were recorded in UTC
tasks.withType<Test>().configureEach {
    systemProperty("user.timezone", "UTC")
    systemProperty("goldens.dir", "$projectDir/src/commonTest/resources/golden")
    // Roborazzi writes the screenshots only in record mode
    systemProperty("roborazzi.test.record", "true")
    systemProperty("screenshots.dir", "${layout.buildDirectory.get().asFile}/screenshots/android")
}
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>().configureEach {
    environment("SIMCTL_CHILD_GOLDENS_DIR", "$projectDir/src/commonTest/resources/golden")
    environment("SIMCTL_CHILD_TZ", "UTC")
    environment("SIMCTL_CHILD_SCREENSHOTS_DIR", "${layout.buildDirectory.get().asFile}/screenshots/ios")
    doFirst { layout.buildDirectory.dir("screenshots/ios").get().asFile.mkdirs() }
}
