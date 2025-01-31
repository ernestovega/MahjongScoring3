package dialogs

import androidx.compose.runtime.Immutable
import screens.help.diffs.Diff

@Immutable
data class DiffsState(
    val playerDiffs: Diff,
)