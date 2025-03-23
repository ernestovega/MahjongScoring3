package di

import app.cash.sqldelight.db.SqlDriver
import com.etologic.mahjongscoring.DbGame
import com.etologic.mahjongscoring.DbRound
import com.etologic.mahjongscoring.MS3Database
import data.database.sqldelight.DateAdapter
import data.database.sqldelight.TableWindsAdapter
import org.koin.dsl.module

val sharedModule = module {

    //Database
    single<MS3Database> {
        MS3Database(
            driver = createTestDriver(),
            DbGameAdapter = DbGame.Adapter(
                startDateAdapter = DateAdapter,
                endDateAdapter = DateAdapter,
            ),
            DbRoundAdapter = DbRound.Adapter(
                winnerInitialSeatAdapter = TableWindsAdapter,
                discarderInitialSeatAdapter = TableWindsAdapter,
            ),
        )
    }
}
