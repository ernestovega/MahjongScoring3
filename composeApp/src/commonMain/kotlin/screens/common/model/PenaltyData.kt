package screens.common.model

import screens.common.model.enums.TableWinds

data class PenaltyData(
    val points: Int,
    val isDivided: Boolean,
    val penalizedPlayerInitialSeat: TableWinds,
)