package com.etologic.mahjongscoring3.ui.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The window insets the app layout applies (MS2 MainActivity.applyWindowInsets, Android 15/16 edge-to-edge):
 * - [bars]: system bars and display cutouts. Top pads the app bar (painted dark green), bottom pads the bottom
 *   navigation, and the sides pad everything (landscape bars and notches).
 * - [ime]: the keyboard. While it covers the navigation bar it lifts the whole content above it, and the bottom
 *   navigation loses its navigation bar padding.
 */
data class AppInsets(val bars: WindowInsets, val ime: WindowInsets)

/** Lets tests send fake insets, like MS2 EdgeToEdgeTest does with WindowInsetsCompat. */
val LocalAppInsetsOverride = staticCompositionLocalOf<AppInsets?> { null }

@Composable
internal fun currentAppInsets(): AppInsets =
    LocalAppInsetsOverride.current ?: AppInsets(
        bars = WindowInsets.systemBars.union(WindowInsets.displayCutout),
        ime = WindowInsets.ime,
    )
