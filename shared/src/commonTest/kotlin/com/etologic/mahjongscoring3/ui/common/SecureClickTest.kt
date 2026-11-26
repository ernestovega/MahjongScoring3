package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.etologic.mahjongscoring3.ui.UiPlatformTest
import com.etologic.mahjongscoring3.ui.runAppUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** MS2 View.setOnSecureClickListener: a second click within 600ms of the last accepted one is ignored. */
class SecureClickTest : UiPlatformTest() {

    private var time = 0L
    private val clock = { time }

    @Test
    fun guard_ignores_clicks_within_600ms_like_MS2() {
        val guard = SecureClickGuard(now = clock)
        var clicks = 0
        guard.click { clicks++ }
        time += 599
        guard.click { clicks++ }
        assertEquals(1, clicks)
        time += 1
        guard.click { clicks++ }
        assertEquals(2, clicks)
    }

    @Test
    fun guard_measures_from_the_end_of_the_action_like_MS2() {
        val guard = SecureClickGuard(now = clock)
        var clicks = 0
        guard.click { clicks++; time += 1000 } // a slow action
        time += 300
        guard.click { clicks++ }
        assertEquals(1, clicks)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun double_tap_on_a_composable_runs_the_action_once() = runAppUiTest {
        var clicks = 0
        setContent {
            CompositionLocalProvider(LocalSecureClickClock provides clock) {
                Box(Modifier.size(48.dp).testTag("button").clickable(onClick = rememberSecureClick { clicks++ }))
            }
        }
        onNodeWithTag("button").performClick()
        onNodeWithTag("button").performClick()
        waitForIdle()
        assertEquals(1, clicks)
        time += SECURE_CLICK_DEBOUNCE_MS
        onNodeWithTag("button").performClick()
        waitForIdle()
        assertEquals(2, clicks)
    }
}
