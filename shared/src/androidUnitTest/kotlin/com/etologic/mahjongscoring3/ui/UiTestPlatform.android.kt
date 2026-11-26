package com.etologic.mahjongscoring3.ui

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import android.content.ContentProvider
import org.robolectric.Robolectric
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
actual abstract class UiPlatformTest actual constructor()

@OptIn(ExperimentalTestApi::class)
actual fun runAppUiTest(block: ComposeUiTest.() -> Unit) {
    installComposeResourcesContext()
    runComposeUiTest { block() }
}

/**
 * Compose resources get the Android context from its AndroidContextProvider, which Robolectric
 * does not start for a library module. Start it by hand (the class is internal, so by name).
 */
@Suppress("UNCHECKED_CAST")
private fun installComposeResourcesContext() {
    val provider = Class.forName("org.jetbrains.compose.resources.AndroidContextProvider") as Class<ContentProvider>
    Robolectric.setupContentProvider(provider)
}

actual fun saveScreenshot(name: String, image: ImageBitmap) {
    val dir = File(System.getProperty("screenshots.dir") ?: "build/screenshots/android").apply { mkdirs() }
    File(dir, "$name.png").outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
}

/** The Compose resources context is set in [runAppUiTest]. */
@Composable
actual fun UiTestHost(content: @Composable () -> Unit) = content()
