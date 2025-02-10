package domain.model

import domain.model.enums.TableWinds

data class PenaltyData(
    val points: Int,
    val isDivided: Boolean,
    val penalizedPlayerInitialSeat: TableWinds,
)