package com.etologic.mahjongscoring3.ui

import com.etologic.mahjongscoring3.ui.common.LocalSecureClickClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
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

@OptIn(ExperimentalTestApi::class)
private fun runSkikoTest(screen: TestScreen, block: ComposeUiTest.() -> Unit) {
    val density = screen.dpi / 160f
    runSkikoComposeUiTest(size = Size(screen.widthDp * density, screen.heightDp * density), density = Density(density)) { block() }
}

@OptIn(ExperimentalTestApi::class)
actual fun runAppUiTest(screen: TestScreen, block: ComposeUiTest.() -> Unit) {
    val owner = TestOwner()
    currentOwner = owner
    try {
        runSkikoTest(screen, block)
    } finally {
        owner.viewModelStore.clear()
    }
}

/**
 * The Skiko test window has a fixed size, and dialogs are laid out in that size, so a new test window of the
 * [screen] size runs [block]. The ViewModelStoreOwner stays (see [currentOwner]), like in a rotation.
 */
@OptIn(ExperimentalTestApi::class)
actual fun ComposeUiTest.withScreen(screen: TestScreen, block: ComposeUiTest.() -> Unit) = runSkikoTest(screen, block)

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

/**
 * The owner of the running test. It is created outside the composition (in [runAppUiTest]), like an
 * activity: a StateRestorationTester save and restore rebuilds the content but keeps the ViewModels.
 */
private var currentOwner = TestOwner()

@Composable
actual fun UiTestHost(content: @Composable () -> Unit) {
    val owner = currentOwner
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides owner,
        LocalLifecycleOwner provides owner,
        LocalSecureClickClock provides remember { noDebounceClock() },
    ) { content() }
}

actual fun pumpMainThread() = Unit

@OptIn(ExperimentalTestApi::class, androidx.compose.ui.test.ExperimentalTestApi::class)
actual fun ComposeUiTest.pressBack() {
    val roots = onAllNodes(isRoot())
    val top = roots.fetchSemanticsNodes().size - 1
    roots[top].performKeyInput { pressKey(androidx.compose.ui.input.key.Key.Escape) }
    waitForIdle()
}
