package screens.common.model

import screens.common.model.enums.TableWinds
import screens.common.model.enums.TableWinds.NONE

data class BestHand(
    val roundNumber: Int = 0,
    val handValue: Int = 0,
    val playerInitialPosition: TableWinds = NONE,
    val playerName: String = "",
)
