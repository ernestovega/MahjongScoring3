import app.cash.sqldelight.db.SqlDriver
import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.DbRound
import com.etologic.mahjongscoring.MS3Database
import data.database.sqldelight.DateAdapter
import data.database.sqldelight.TableWindsAdapter

expect fun createTestDriver(): SqlDriver

fun createTestDatabase(): MS3Database {
    val driver = createTestDriver()
    return MS3Database(
        driver = driver,
        DbGameAdapter = DbGame.Adapter(
            startDateAdapter = DateAdapter,
            endDateAdapter = DateAdapter
        ),
        DbRoundAdapter = DbRound.Adapter(
            winnerInitialSeatAdapter = TableWindsAdapter,
            discarderInitialSeatAdapter = TableWindsAdapter
        )
    )
}