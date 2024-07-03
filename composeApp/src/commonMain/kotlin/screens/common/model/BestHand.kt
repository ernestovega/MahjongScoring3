package screens.common.model

import screens.common.model.TableWinds
import screens.common.model.TableWinds.NONE

data class BestHand(
    val roundNumber: Int = 0,
    val handValue: Int = 0,
    val playerInitialPosition: TableWinds = NONE,
    val playerName: String = "",
)
