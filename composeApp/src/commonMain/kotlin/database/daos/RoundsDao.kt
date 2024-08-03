package database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import database.tables.DbRound
import kotlinx.coroutines.flow.Flow
import screens.common.ui.GameId
import screens.common.ui.RoundId

@Dao
interface RoundsDao {

    @Query("SELECT * FROM Rounds")
    fun getAllFlow(): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getGameRoundsFlow(gameId: GameId): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId ORDER BY roundId ASC")
    suspend fun getGameRounds(gameId: GameId): List<DbRound>

    @Query("SELECT * FROM Rounds WHERE roundId = :roundId")
    suspend fun getOne(roundId: RoundId): DbRound

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(round: DbRound): RoundId

    @Update
    suspend fun updateOne(round: DbRound): Int

    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    suspend fun deleteGameRounds(gameId: GameId): Int

    @Query("DELETE FROM Rounds WHERE roundId = :roundId")
    suspend fun deleteOne(roundId: RoundId): Int
}
