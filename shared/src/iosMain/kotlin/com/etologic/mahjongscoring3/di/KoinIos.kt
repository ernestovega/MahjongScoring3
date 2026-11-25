package com.etologic.mahjongscoring3.di

import androidx.room.Room
import com.etologic.mahjongscoring3.data.database.AppDatabase
import com.etologic.mahjongscoring3.data.database.DATABASE_NAME
import com.etologic.mahjongscoring3.data.database.buildAppDatabase
import com.etologic.mahjongscoring3.data.settings.DATASTORE_FILE_NAME
import com.etologic.mahjongscoring3.data.settings.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single<AppDatabase> {
        buildAppDatabase(Room.databaseBuilder<AppDatabase>(name = "${documentsDirectory()}/$DATABASE_NAME"))
    }
    single {
        createDataStore { "${documentsDirectory()}/$DATASTORE_FILE_NAME" }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String {
    val url: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path)
}

/** Called from Swift before the first Compose view: `KoinIosKt.doInitKoinIos()`. */
fun initKoinIos() = initKoin()
