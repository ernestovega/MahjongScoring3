package data.repositories.rounds

import data.database.tables.DbRound
import domain.model.enums.TableWinds
import domain.model.exceptions.RoundNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ui.common.components.GameId
import ui.common.components.RoundId

class FakeRoundsRepository : RoundsRepository {

    private var fakeRounds = listOf(
        DbRound(
            gameId = 7,
            roundId = 32,
        ),
        DbRound(
            gameId = 6,
            roundId = 31,
            winnerInitialSeat = null,
            discarderInitialSeat = null,
            handPoints = 0,
            penaltyP1 = 20,
            penaltyP2 = 20,
            penaltyP3 = 20,
            penaltyP4 = -60,
        ),
        DbRound(
            gameId = 6,
            roundId = 30,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 29,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 28,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 27,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 26,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 25,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 24,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 23,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 22,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 21,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 20,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 19,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 18,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 17,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 6,
            roundId = 16,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
            penaltyP1 = 10,
            penaltyP2 = 10,
            penaltyP3 = 10,
            penaltyP4 = -30,
        ),
        DbRound(
            gameId = 5,
            roundId = 15,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 5,
            roundId = 14,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 5,
            roundId = 13,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 5,
            roundId = 12,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 5,
            roundId = 11,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 4,
            roundId = 10,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 4,
            roundId = 9,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 4,
            roundId = 8,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 4,
            roundId = 7,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            roundId = 6,
            gameId = 3,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 3,
            roundId = 5,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 3,
            roundId = 4,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 2,
            roundId = 3,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 2,
            roundId = 2,
            winnerInitialSeat = TableWinds.EAST,
            discarderInitialSeat = TableWinds.SOUTH,
            handPoints = 12,
        ),
        DbRound(
            gameId = 1,
            roundId = 1,
        ),
    ).sortedBy { it.roundId }

    override fun getAllFlow(): Flow<List<DbRound>> = flowOf(fakeRounds)

    override fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>> =
        flowOf(fakeRounds.filter { it.gameId == gameId })

    override suspend fun getGameRounds(gameId: GameId): Result<List<DbRound>> =
        runCatching { fakeRounds.filter { it.gameId == gameId } }

    override suspend fun getOne(roundId: RoundId): Result<DbRound> =
        runCatching { fakeRounds.find { it.roundId == roundId } ?: throw RoundNotFoundException(roundId) }

    override suspend fun insertOne(dbRound: DbRound): Result<Boolean> = runCatching { true }

    override suspend fun updateOne(dbRound: DbRound): Result<Boolean> = runCatching { true }

    override suspend fun deleteGameRounds(gameId: GameId): Result<Boolean> = runCatching { true }

    override suspend fun deleteOne(roundId: RoundId): Result<Boolean> = runCatching { true }
}
