import android.os.Build
import androidx.test.core.app.ApplicationProvider
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.etologic.mahjongscoring.MS3Database

actual fun createTestDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = MS3Database.Schema,
        context = ApplicationProvider.getApplicationContext(),
        name = "test.db",
        useNoBackupDirectory = true,
    )
}