import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.etologic.mahjongscoring.MS3Database

actual fun createTestDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = MS3Database.Schema,
        name = "test.db",
        onConfiguration = { it.copy(inMemory = true) },
    )
}