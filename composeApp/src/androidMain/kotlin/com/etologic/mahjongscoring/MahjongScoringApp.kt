package com.etologic.mahjongscoring

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

class MahjongScoringApp: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MahjongScoringApp)
        }
    }
}