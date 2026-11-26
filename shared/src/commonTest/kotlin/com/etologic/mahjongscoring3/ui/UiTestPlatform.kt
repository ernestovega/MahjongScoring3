package com.etologic.mahjongscoring3.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

/**
 * Base class of the UI tests. Android: Robolectric with native graphics on the same device as the MS2
 * screenshots (411x891dp, 420dpi). iOS: nothing (the test window is sized in [runAppUiTest]).
 */
expect abstract class UiPlatformTest()

/**
 * The screens the UI tests run on. [androidQualifiers] are the Robolectric qualifiers of MS2 UiTestBase.DEVICE,
 * EdgeToEdgeTest.PHONE_LANDSCAPE and LargeScreenTest.TABLET_LANDSCAPE; iOS sizes its test window the same.
 */
enum class TestScreen(val widthDp: Int, val heightDp: Int, val dpi: Int, val androidQualifiers: String) {
    /** Pixel 7 class phone in portrait, the screen of every MS2 UI test. */
    PHONE(411, 891, 420, "w411dp-h891dp-normal-long-notround-any-420dpi-keyshidden-nonav"),
    PHONE_LANDSCAPE(891, 411, 420, "w891dp-h411dp-normal-long-notround-land-420dpi-keyshidden-nonav"),
    /** 10" tablet: Android 16 ignores the portrait lock on screens with a smallest width of 600dp or more. */
    TABLET_LANDSCAPE(1280, 800, 160, "w1280dp-h800dp-xlarge-long-notround-land-mdpi-keyshidden-nonav"),
    TABLET_PORTRAIT(800, 1280, 160, "w800dp-h1280dp-xlarge-long-notround-port-mdpi-keyshidden-nonav"),
}

/** Runs a Compose UI test on [screen] (by default a 411x891dp, 420dpi phone, like the MS2 UI tests). */
@OptIn(ExperimentalTestApi::class)
expect fun runAppUiTest(screen: TestScreen = TestScreen.PHONE, block: ComposeUiTest.() -> Unit)

/**
 * Runs [block] on a window with the size of [screen] (a rotation or a window resize), with the same density.
 * Android resizes the open windows and runs [block] on this test; iOS cannot resize its test window, so [block]
 * runs on a new one, without content: the caller sets it again (UiTestBase.rotate() does it).
 */
@OptIn(ExperimentalTestApi::class)
expect fun ComposeUiTest.withScreen(screen: TestScreen, block: ComposeUiTest.() -> Unit)

/**
 * Captures the whole screen, dialogs and menus included, as <screenshots dir>/<name>.png
 * (shared/build/screenshots/android or shared/build/screenshots/ios).
 */
@OptIn(ExperimentalTestApi::class)
expect fun ComposeUiTest.captureScreen(name: String)

/**
 * Wraps the app content with what the platform test host does not provide on its own
 * (Android: the Compose resources context; iOS: a ViewModelStoreOwner).
 */
@Composable
expect fun UiTestHost(content: @Composable () -> Unit)

/**
 * Lets background work (Room, DataStore) hand its results to the main thread while a test waits.
 * Android: Robolectric runs the test on a paused main looper with a fake clock, so it sleeps a little and runs
 * the looper 50ms ahead (MS2 UiTestBase.settle() did the same). iOS: nothing.
 */
expect fun pumpMainThread()

/**
 * The app flows tap like a fast user; MS2's Espresso tests never hit its 600ms anti double click either.
 * Every read moves this clock a full debounce ahead, so the guard never drops a test tap.
 * [com.etologic.mahjongscoring3.ui.common.SecureClickTest] tests the guard itself.
 */
fun noDebounceClock(): () -> Long {
    var time = 0L
    return { time += com.etologic.mahjongscoring3.ui.common.SECURE_CLICK_DEBOUNCE_MS; time }
}

/** The system back action (Android back button or gesture). iOS has no back key: Escape closes dialogs there. */
@OptIn(ExperimentalTestApi::class)
expect fun ComposeUiTest.pressBack()
