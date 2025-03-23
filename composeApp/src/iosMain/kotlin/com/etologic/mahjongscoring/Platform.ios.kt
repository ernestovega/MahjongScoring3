package com.etologic.mahjongscoring

import platform.UIKit.UIDevice
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.etologic.mahjongscoring.common.components.DB_NAME

class IOSPlatform: Platform {
    override val name: String = with (UIDevice.currentDevice) { "${systemName()} $systemVersion" }
}

actual fun getPlatform(): Platform = IOSPlatform()

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        NativeSqliteDriver(MS3Database.Schema, DB_NAME)
}
