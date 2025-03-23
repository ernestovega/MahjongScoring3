package com.etologic.mahjongscoring

import android.content.Context
import android.os.Build
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.etologic.mahjongscoring.common.components.DB_NAME

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

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