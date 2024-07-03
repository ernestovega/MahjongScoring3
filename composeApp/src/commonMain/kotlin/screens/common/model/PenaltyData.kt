package screens.common.model

import screens.common.model.TableWinds

data class PenaltyData(
    val points: Int,
    val isDivided: Boolean,
    val penalizedPlayerInitialSeat: TableWinds,
)