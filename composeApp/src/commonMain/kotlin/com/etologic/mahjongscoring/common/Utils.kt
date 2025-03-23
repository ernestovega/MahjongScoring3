package com.etologic.mahjongscoring.common

import domain.model.UiGame
import domain.model.UiRound
import kotlinx.coroutines.flow.StateFlow

suspend fun StateFlow<UiGame?>.with(
    predicate: suspend (UiGame) -> Result<Unit>
): Result<Unit> =
    this.value?.let { predicate(it) }
        ?: Result.failure(Exception("Game not found"))

suspend fun StateFlow<UiGame?>.withOngoingRound(
    predicate: suspend (UiRound) -> Result<Unit>
): Result<Unit> =
    this.value?.let { predicate(it.ongoingOrLastRound) }
        ?: Result.failure(Exception("Game not found"))