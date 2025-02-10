package ui.dialogs.hand_actions

import androidx.compose.runtime.Immutable
import ui.screens.help.diffs.Diff

@Immutable
data class DiffsState(
    val playerDiffs: Diff,
)