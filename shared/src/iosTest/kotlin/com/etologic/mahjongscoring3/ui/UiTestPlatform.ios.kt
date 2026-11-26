package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.posix.getenv

actual abstract class UiPlatformTest actual constructor()

private const val DENSITY = 420f / 160f

@OptIn(ExperimentalTestApi::class)
actual fun runAppUiTest(block: ComposeUiTest.() -> Unit) =
    runSkikoComposeUiTest(size = Size(411 * DENSITY, 891 * DENSITY), density = Density(DENSITY)) { block() }

@OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
actual fun saveScreenshot(name: String, image: ImageBitmap) {
    val dir = getenv("SCREENSHOTS_DIR")?.toKString() ?: return
    val bytes = Image.makeFromBitmap(image.asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG)?.bytes ?: return
    val data = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
    data.writeToFile("$dir/$name.png", atomically = true)
}
