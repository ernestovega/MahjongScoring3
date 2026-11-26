package com.etologic.mahjongscoring3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi

/**
 * Emulates an activity recreation for Compose content, on Android and iOS alike: the saveable state of
 * the content is saved, the content leaves the composition, and it is built again from the saved state.
 * Whatever is outside the content (the ViewModelStoreOwner, so the ViewModels) stays, like in a recreation.
 *
 * Same mechanism as androidx.compose.ui.test.StateRestorationTester, which cannot be used here because the
 * iOS (Skiko) test host has no SaveableStateRegistry for it to wrap. On Android the values must still be
 * savable by the activity's registry (Bundle types), as in a real recreation.
 */
class StateRestorer {
    private var isShown by mutableStateOf(true)

    /** The test window that shows the content (null: the first one). See [moveTo]. */
    private var activeWindow by mutableStateOf<Any?>(null)
    private var savedState: Map<String, List<Any?>>? = null
    private var registry: SaveableStateRegistry? = null

    @Composable
    fun Content(window: Any? = null, content: @Composable () -> Unit) {
        if (!isShown || window != activeWindow) return
        val parent = LocalSaveableStateRegistry.current
        val restored = savedState
        val newRegistry = remember { SaveableStateRegistry(restored) { parent?.canBeSaved(it) ?: true } }
        registry = newRegistry
        CompositionLocalProvider(LocalSaveableStateRegistry provides newRegistry, content = content)
    }

    @OptIn(ExperimentalTestApi::class)
    fun ComposeUiTest.saveAndRestore() {
        saveAndRemove()
        isShown = true
        waitForIdle()
    }

    /** Saves the state and removes the content. [Content] then shows it again from the saved state. */
    @OptIn(ExperimentalTestApi::class)
    fun ComposeUiTest.saveAndRemove() {
        savedState = checkNotNull(registry) { "The content is not shown" }.performSave()
        isShown = false
        waitForIdle()
        registry = null
    }

    /** The content removed by [saveAndRemove] is shown again only in [window] (a new test window). */
    fun moveTo(window: Any) {
        activeWindow = window
    }

    /** Shows the content removed by [saveAndRemove] again. */
    @OptIn(ExperimentalTestApi::class)
    fun ComposeUiTest.restore() {
        isShown = true
        waitForIdle()
    }
}
