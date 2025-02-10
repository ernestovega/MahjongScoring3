package domain.model

import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.NONE

data class HuData(
    val points: Int,
    val winnerInitialSeat: TableWinds,
    val discarderInitialSeat: TableWinds = NONE,
)