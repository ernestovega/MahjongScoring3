package com.etologic.mahjongscoring3.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {
    @Query("SELECT * from Games ORDER BY startDate DESC, gameId DESC")
    fun getAllFlow(): Flow<List<DbGame>>

    @Query("SELECT * from Games WHERE gameId = :gameId")
    fun getOneFlow(gameId: Long): Flow<DbGame?>

    @Query("SELECT * from Games WHERE gameId = :gameId")
    suspend fun getOne(gameId: Long): DbGame?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(dbGame: DbGame): Long

    @Update
    suspend fun updateOne(dbGame: DbGame): Int

    @Query("DELETE FROM Games WHERE gameId = :gameId")
    suspend fun deleteOne(gameId: Long): Int
}

@Dao
interface RoundsDao {
    @Query("SELECT * FROM Rounds")
    fun getAllFlow(): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId")
    fun getGameRoundsFlow(gameId: Long): Flow<List<DbRound>>

    @Query("SELECT * FROM Rounds WHERE gameId = :gameId ORDER BY roundId ASC")
    suspend fun getGameRounds(gameId: Long): List<DbRound>

    @Query("SELECT * FROM Rounds WHERE roundId = :roundId")
    suspend fun getOne(roundId: Long): DbRound?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(round: DbRound): Long

    @Update
    suspend fun updateOne(round: DbRound): Int

    @Query("DELETE FROM Rounds WHERE gameId = :gameId")
    suspend fun deleteGameRounds(gameId: Long): Int

    @Query("DELETE FROM Rounds WHERE roundId = :roundId")
    suspend fun deleteOne(roundId: Long): Int
}
