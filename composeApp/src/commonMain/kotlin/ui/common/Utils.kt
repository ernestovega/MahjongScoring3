package ui.common

import domain.model.UiGame
import domain.model.UiRound
import kotlinx.coroutines.flow.StateFlow

suspend fun StateFlow<UiGame?>.with(
    predicate: suspend (UiGame) -> Result<Boolean>
): Result<Boolean> =
    this.value?.let { predicate(it) }
        ?: Result.failure(Exception("Game not found"))

suspend fun StateFlow<UiGame?>.withOngoingRound(
    predicate: suspend (UiRound) -> Result<Boolean>
): Result<Boolean> =
    this.value?.let { predicate(it.ongoingOrLastRound) }
        ?: Result.failure(Exception("Game not found"))