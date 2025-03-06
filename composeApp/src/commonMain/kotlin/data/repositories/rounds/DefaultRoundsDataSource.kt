package data.repositories.rounds

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.etologic.mahjongscoring.DbRound
import com.etologic.mahjongscoring.MS3Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DefaultRoundsDataSource(db: MS3Database): RoundsDataSource {

    private val queries = db.dbRoundQueries

    override fun getAllFlow(): Flow<List<DbRound>> =
        queries.getAllRoundsFlow()
            .asFlow()
            .mapToList(Dispatchers.IO)

    override fun getGameRoundsFlow(gameId: Long): Flow<List<DbRound>> =
        queries.getGameRoundsFlow(gameId)
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun getGameRounds(gameId: Long): List<DbRound> =
        withContext(Dispatchers.IO) {
            queries.getGameRounds(gameId)
                .executeAsList()
        }

    override suspend fun getOne(roundId: Long): DbRound =
        withContext(Dispatchers.IO) {
            queries.getOne(roundId)
                .executeAsOne()
        }

    override suspend fun insertOne(round: DbRound): Long =
        withContext(Dispatchers.IO) {
            queries.insertOne(
                gameId = round.gameId,
                winnerInitialSeat = round.winnerInitialSeat,
                discarderInitialSeat = round.discarderInitialSeat,
                handPoints = round.handPoints,
                penaltyP1 = round.penaltyP1,
                penaltyP2 = round.penaltyP2,
                penaltyP3 = round.penaltyP3,
                penaltyP4 = round.penaltyP4
            )
            queries.lastInsertRowId().executeAsOne()
        }

    override suspend fun updateOne(round: DbRound) {
        withContext(Dispatchers.IO) {
            queries.updateOne(
                gameId = round.gameId,
                winnerInitialSeat = round.winnerInitialSeat,
                discarderInitialSeat = round.discarderInitialSeat,
                handPoints = round.handPoints,
                penaltyP1 = round.penaltyP1,
                penaltyP2 = round.penaltyP2,
                penaltyP3 = round.penaltyP3,
                penaltyP4 = round.penaltyP4,
                roundId = round.roundId
            )
        }
    }

    override suspend fun deleteGameRounds(gameId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteGameRounds(gameId)
        }
    }

    override suspend fun deleteOne(roundId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteOne(roundId)
        }
    }
}