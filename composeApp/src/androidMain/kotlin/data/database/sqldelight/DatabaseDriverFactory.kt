package data.database.sqldelight

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.etologic.mahjongscoring.MS3Database
import ui.common.components.DB_NAME

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory actual constructor() {

    private lateinit var context: Context

    constructor(context: Context) : this() {
        this.context = context
    }

    actual fun create(): SqlDriver =
        AndroidSqliteDriver(
            schema = MS3Database.Schema,
            context = context,
            name = DB_NAME,
        )
}
