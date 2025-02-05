package com.etologic.mahjongscoring.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import database.AppDatabase
import screens.common.ui.APP_DATABASE_NAME

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
