package com.etologic.mahjongscoring3.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.etologic.mahjongscoring3.ui.main.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Replaces the branded launch background (MS2 splash) with the normal one
        setTheme(R.style.AppTheme)
        // Same as MS2: apps targeting Android 15+ are always edge-to-edge, so do it on every version.
        // Transparent bars with light icons (the app bar and the bottom navigation are dark green);
        // App applies the insets.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
