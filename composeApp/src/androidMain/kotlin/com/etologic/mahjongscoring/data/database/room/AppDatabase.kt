package com.etologic.mahjongscoring.data.database.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import data.database.room.AppDatabase
import ui.common.components.APP_DATABASE_NAME

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
