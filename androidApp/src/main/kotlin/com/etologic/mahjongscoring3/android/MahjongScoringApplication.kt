package com.etologic.mahjongscoring3.android

import android.app.Application
import com.etologic.mahjongscoring3.di.initKoin
import org.koin.android.ext.koin.androidContext

class MahjongScoringApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MahjongScoringApplication)
        }
    }
}
