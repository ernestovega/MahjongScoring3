package com.etologic.mahjongscoring3.ui

import androidx.compose.runtime.CompositionLocalProvider
import com.etologic.mahjongscoring3.ui.common.LocalSecureClickClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import android.content.ContentProvider
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowDisplayManager
import org.robolectric.shadows.ShadowViewRootImpl
import org.robolectric.shadow.api.Shadow
import android.view.Display
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureScreenRoboImage
import java.io.File
import java.util.concurrent.TimeUnit
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
actual abstract class UiPlatformTest actual constructor()

/** [UiPlatformTest] sets the [TestScreen.PHONE] qualifiers; other screens replace them before the activity starts. */
@OptIn(ExperimentalTestApi::class)
actual fun runAppUiTest(screen: TestScreen, block: ComposeUiTest.() -> Unit) {
    if (screen != TestScreen.PHONE) RuntimeEnvironment.setQualifiers(screen.androidQualifiers)
    installComposeResourcesContext()
    runComposeUiTest { block() }
}

/**
 * MS2 LargeScreenTest used RuntimeEnvironment.setQualifiers, but on an open activity that also recreates it,
 * and the recreated activity has no test content. So only the display changes here, and the open windows are
 * laid out again; UiTestBase.rotate() then emulates the recreation, as on iOS.
 */
@OptIn(ExperimentalTestApi::class)
actual fun ComposeUiTest.withScreen(screen: TestScreen, block: ComposeUiTest.() -> Unit) {
    ShadowDisplayManager.changeDisplay(Display.DEFAULT_DISPLAY, screen.androidQualifiers)
    resizeWindows()
    waitForIdle()
    block()
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

/**
 * Robolectric never draws a frame for captureToImage(), so the Android screenshots use Roborazzi, as MS2 does.
 * captureScreenRoboImage() captures every window: the activity, dialogs and popups.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
actual fun ComposeUiTest.captureScreen(name: String) {
    val dir = File(System.getProperty("screenshots.dir") ?: "build/screenshots/android").apply { mkdirs() }
    captureScreenRoboImage(
        file = File(dir, "$name.png"),
        roborazziOptions = RoborazziOptions(recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5)),
    )
}

/** The Compose resources context is set in [runAppUiTest]. */
@Composable
actual fun UiTestHost(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalSecureClickClock provides remember { noDebounceClock() }, content = content)

actual fun pumpMainThread() {
    Thread.sleep(10)
    ShadowLooper.idleMainLooper(50, TimeUnit.MILLISECONDS)
}

@OptIn(ExperimentalTestApi::class)
actual fun ComposeUiTest.pressBack() {
    androidx.test.espresso.Espresso.pressBack()
    waitForIdle()
}

/** Sends the new display size to every open window (the activity, dialogs and popups) and lays them out again. */
private fun resizeWindows() {
    ShadowLooper.idleMainLooper()
    val global = Class.forName("android.view.WindowManagerGlobal")
    val instance = global.getMethod("getInstance").invoke(null)
    val roots = global.getDeclaredField("mRoots").apply { isAccessible = true }.get(instance) as List<*>
    roots.toList().forEach { viewRoot ->
        Shadow.extract<ShadowViewRootImpl>(viewRoot).callDispatchResized()
    }
    ShadowLooper.idleMainLooper()
}
