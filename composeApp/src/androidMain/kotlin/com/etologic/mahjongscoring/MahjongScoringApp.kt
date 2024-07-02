package com.etologic.mahjongscoring

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext

class MahjongScoringApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MahjongScoringApp)
        }
    }
}