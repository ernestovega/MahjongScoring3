package com.etologic.mahjongscoring3.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

/**
 * Base class of the UI tests. Android: Robolectric with native graphics on the same device as the MS2
 * screenshots (411x891dp, 420dpi). iOS: nothing (the test window is sized in [runAppUiTest]).
 */
expect abstract class UiPlatformTest()

/** Runs a Compose UI test on a 411x891dp, 420dpi screen (Pixel 7 class, like the MS2 UI tests). */
@OptIn(ExperimentalTestApi::class)
expect fun runAppUiTest(block: ComposeUiTest.() -> Unit)

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
