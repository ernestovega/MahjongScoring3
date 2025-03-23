package domain.model

import androidx.compose.runtime.Immutable
import com.etologic.mahjongscoring.common.components.getNeededPointsByDirectHu
import com.etologic.mahjongscoring.common.components.getNeededPointsByIndirectHu
import com.etologic.mahjongscoring.common.components.getNeededPointsBySelfPick

@Immutable
data class Diff(
    val pointsNeeded: Int
) {
    val selfPick: Int = pointsNeeded.getNeededPointsBySelfPick()
    val directHu: Int = pointsNeeded.getNeededPointsByDirectHu()
    val indirectHu: Int = pointsNeeded.getNeededPointsByIndirectHu()
}