package com.etologic.mahjongscoring3.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.TimeSource

/** MS2 setOnSecureClickListener default debounce time. */
const val SECURE_CLICK_DEBOUNCE_MS = 600L

private val clockStart = TimeSource.Monotonic.markNow()

/** Milliseconds of a monotonic clock (MS2 used SystemClock.elapsedRealtime). */
fun monotonicMillis(): Long = clockStart.elapsedNow().inWholeMilliseconds

/** Clock used by [rememberSecureClick]. Tests can replace it. */
val LocalSecureClickClock = staticCompositionLocalOf<() -> Long> { ::monotonicMillis }

/**
 * Same as MS2 View.setOnSecureClickListener: a click less than [debounceMs] after the last accepted
 * one on the same element is ignored. The time is taken again after the action runs, like MS2.
 */
class SecureClickGuard(
    private val debounceMs: Long = SECURE_CLICK_DEBOUNCE_MS,
    private val now: () -> Long,
) {
    private var lastClickTime: Long? = null

    fun click(action: () -> Unit) {
        val last = lastClickTime
        if (last != null && now() - last < debounceMs) return
        action()
        lastClickTime = now()
    }
}

/** Wraps [action] in a [SecureClickGuard] that lives as long as the calling element. */
@Composable
fun rememberSecureClick(debounceMs: Long = SECURE_CLICK_DEBOUNCE_MS, action: () -> Unit): () -> Unit {
    val clock = LocalSecureClickClock.current
    val currentAction by rememberUpdatedState(action)
    val guard = remember(clock, debounceMs) { SecureClickGuard(debounceMs, clock) }
    return remember(guard) { { guard.click { currentAction() } } }
}
