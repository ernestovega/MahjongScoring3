package com.etologic.mahjongscoring3.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
actual abstract class UiPlatformTest actual constructor()

@OptIn(ExperimentalTestApi::class)
actual fun runAppUiTest(block: ComposeUiTest.() -> Unit) = runComposeUiTest { block() }

actual fun saveScreenshot(name: String, image: ImageBitmap) {
    val dir = File(System.getProperty("screenshots.dir") ?: "build/screenshots/android").apply { mkdirs() }
    File(dir, "$name.png").outputStream().use { image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
}
