package com.etologic.mahjongscoring3.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
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

/** Writes [image] as <screenshots dir>/<name>.png (build/screenshots/android or build/screenshots/ios). */
expect fun saveScreenshot(name: String, image: ImageBitmap)

/**
 * Wraps the app content with what the platform test host does not provide on its own
 * (Android: the Compose resources context; iOS: a ViewModelStoreOwner).
 */
@Composable
expect fun UiTestHost(content: @Composable () -> Unit)
