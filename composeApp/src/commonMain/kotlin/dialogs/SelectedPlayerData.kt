package dialogs

import androidx.compose.runtime.Immutable
import screens.common.model.enums.TableWinds

@Immutable
data class SelectedPlayerData(
    val currentSeat: TableWinds,
    val initialSeat: TableWinds,
    val name: String,
    val points: Int,
)