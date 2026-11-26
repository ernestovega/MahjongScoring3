package com.etologic.mahjongscoring3.ui

import com.etologic.mahjongscoring3.ui.common.LocalSecureClickClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
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

/** On iOS dialogs and popups are drawn in the same scene, so the first root has the whole screen. */
@OptIn(ExperimentalTestApi::class)
actual fun ComposeUiTest.captureScreen(name: String) {
    savePng(name, onAllNodes(isRoot())[0].captureToImage())
}

@OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
private fun savePng(name: String, image: ImageBitmap) {
    val dir = getenv("SCREENSHOTS_DIR")?.toKString() ?: return
    val bytes = Image.makeFromBitmap(image.asSkiaBitmap()).encodeToData(EncodedImageFormat.PNG)?.bytes ?: return
    val data = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
    data.writeToFile("$dir/$name.png", atomically = true)
}

/** ComposeUIViewController provides these owners in the app; the Skiko test host does not. */
private class TestOwner : ViewModelStoreOwner, LifecycleOwner {
    override val viewModelStore = ViewModelStore()
    private val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
    override val lifecycle: Lifecycle get() = registry
}

@Composable
actual fun UiTestHost(content: @Composable () -> Unit) {
    val owner = remember { TestOwner() }
    DisposableEffect(owner) { onDispose { owner.viewModelStore.clear() } }
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides owner,
        LocalLifecycleOwner provides owner,
        LocalSecureClickClock provides remember { noDebounceClock() },
    ) { content() }
}

actual fun pumpMainThread() = Unit
