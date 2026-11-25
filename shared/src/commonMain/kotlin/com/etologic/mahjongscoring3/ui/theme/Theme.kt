package com.etologic.mahjongscoring3.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Mahjong Scoring 2 palette (res/values/colors.xml). */
object MmColors {
    val Primary = Color(0xFF00B16A)
    val PrimaryDark = Color(0xFF007B49)
    val Accent = Color(0xFFFF4E95)
    val White = Color(0xFFFFFFFF)
    val WhiteTransparent = Color(0x88FFFFFF)
    val BlackTransparent = Color(0xBB000000)
    val GrayLight = Color(0xFFEEEEEE)
    val Gray = Color(0xFFBBBBBB)
    val GrayDiffs = Color(0xFF888888)
    val GrayMM = Color(0xFF414141)
    val Red = Color(0xFFFF0000)
    val Golden = Color(0xFFFFC107)
    val PurplePenalty = Color(0xFF7C00E7)
}

/** Mahjong Scoring 2 dimens (res/values/dimens.xml). */
object MmDimens {
    val QuarterMargin = 4.dp
    val HalfMargin = 8.dp
    val HalfAndQuarterMargin = 12.dp
    val StandardMargin = 16.dp
    val StandardAndHalfMargin = 24.dp
    val DoubleMargin = 32.dp
    val DoubleAndHalfMargin = 40.dp
    val TripleMargin = 48.dp
    val QuadrupleMargin = 64.dp
    val CardCornerRadius = 4.dp
    val DrawerHeaderHeight = 160.dp
    val TablePlayerPoints = 36.sp
    val TablePlayerPenaltyPoints = 20.sp
    val TableRoundNumber = 28.sp
}

private val MmLightColors = lightColors(
    primary = MmColors.Primary,
    primaryVariant = MmColors.PrimaryDark,
    secondary = MmColors.Accent,
    secondaryVariant = MmColors.Accent,
    onPrimary = MmColors.White,
    onSecondary = MmColors.White,
    background = MmColors.GrayLight,
    surface = MmColors.GrayLight,
    onBackground = MmColors.GrayMM,
    onSurface = MmColors.GrayMM,
)

@Composable
fun MahjongScoringTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = MmLightColors,
        content = content,
    )
}
