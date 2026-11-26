package com.etologic.mahjongscoring3.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.etologic.mahjongscoring3.ui.main.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Replaces the branded launch background (MS2 splash) with the normal one
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
