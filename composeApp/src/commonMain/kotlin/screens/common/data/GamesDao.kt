package screens.common.data

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
import screens.common.model.GameId
import kotlinx.coroutines.flow.Flow

//@Dao
interface GamesDao {

//    @Query("SELECT * from Games ORDER BY startDate DESC, gameId DESC")
    fun getAllFlow(): Flow<List<DbGame>>

//    @Query("SELECT * from Games WHERE gameId = :gameId")
    fun getOneFlow(gameId: GameId): Flow<DbGame>

//    @Query("SELECT * from Games WHERE gameId = :gameId")
    suspend fun getOne(gameId: GameId): DbGame

//    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOne(dbGame: DbGame): GameId

//    @Update
    suspend fun updateOne(dbGame: DbGame): Int

//    @Query("DELETE FROM Games WHERE gameId = :gameId")
    suspend fun deleteOne(gameId: GameId): Int
}
