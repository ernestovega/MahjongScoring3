package screens.common.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import screens.common.data.DbGame

@Serializable
data class PortableGame(
    val gameName: String,
    val startDate: Long,
    val endDate: Long?,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
    val rounds: List<PortableRound> = emptyList(),
)

fun UiGame.toPortableGame() = PortableGame(
    gameName = gameName,
    startDate = startDate.toEpochMilliseconds(),
    endDate = endDate?.toEpochMilliseconds(),
    nameP1 = nameP1,
    nameP2 = nameP2,
    nameP3 = nameP3,
    nameP4 = nameP4,
    rounds = uiRounds.map { it.toPortableRound() },
)

fun PortableGame.toDbGame(gameId: GameId): DbGame =
    DbGame(
        gameId = gameId,
        nameP1 = nameP1,
        nameP2 = nameP2,
        nameP3 = nameP3,
        nameP4 = nameP4,
        startDate = Instant.fromEpochMilliseconds(startDate),
        endDate = endDate?.let { Instant.fromEpochMilliseconds(it) },
        gameName = gameName,
    )
