package screens.common.model

import screens.common.model.TableWinds
import screens.common.model.TableWinds.NONE

data class HuData(
    val points: Int,
    val winnerInitialSeat: TableWinds,
    val discarderInitialSeat: TableWinds = NONE,
)