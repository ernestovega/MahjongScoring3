package screens.common.model

import screens.common.model.enums.TableWinds
import screens.common.model.enums.TableWinds.NONE

data class HuData(
    val points: Int,
    val winnerInitialSeat: TableWinds,
    val discarderInitialSeat: TableWinds = NONE,
)