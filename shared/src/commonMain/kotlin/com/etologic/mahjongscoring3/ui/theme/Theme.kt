package com.etologic.mahjongscoring3.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlin.math.floor
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
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

/**
 * Android TextView defaults used by MS2 layouts: 14sp and no letter spacing (Material's defaults are
 * 16sp body text with 0.15-0.5sp letter spacing, which made every MS3 text wider than in MS2).
 */
/**
 * MS2 uses plain Android TextViews: no fixed line height (Material's 24sp/20sp/16sp line heights made every
 * MS3 row taller) and, on Android, the TextView default font padding.
 */
private val MmTypography = Typography().let { t ->
    fun TextStyle.ms2(): TextStyle = copy(lineHeight = TextUnit.Unspecified, platformStyle = mmPlatformTextStyle)
    t.copy(
        h1 = t.h1.ms2(),
        h2 = t.h2.ms2(),
        h3 = t.h3.ms2(),
        h4 = t.h4.ms2(),
        h5 = t.h5.ms2(),
        h6 = t.h6.copy(letterSpacing = 0.sp).ms2(),
        subtitle1 = t.subtitle1.copy(letterSpacing = 0.sp).ms2(),
        subtitle2 = t.subtitle2.copy(letterSpacing = 0.sp).ms2(),
        body1 = t.body1.copy(fontSize = 14.sp, letterSpacing = 0.sp).ms2(),
        body2 = t.body2.copy(letterSpacing = 0.sp).ms2(),
        button = t.button.copy(letterSpacing = 0.sp).ms2(), // MS2 buttons have no extra letter spacing
        caption = t.caption.copy(letterSpacing = 0.sp).ms2(),
        overline = t.overline.ms2(),
    )
}

/** Android: includeFontPadding = true like a TextView. iOS: default. */
internal expect val mmPlatformTextStyle: PlatformTextStyle?

@Composable
fun MahjongScoringTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDensity provides ms2TextDensity(LocalDensity.current)) {
        MaterialTheme(
            colors = MmLightColors,
            typography = MmTypography,
            content = content,
        )
    }
}

/**
 * MS2 TextViews round their text size to whole pixels: 14sp is 36.75px on a 420dpi phone and MS2 draws it at 37px,
 * so each line is 51px tall instead of 49px. Compose keeps the fraction, and its text layout only keeps the density
 * and the font scale of [LocalDensity], so the font scale is adjusted: 14sp, the size of most MS2 texts, gives the same
 * whole number of pixels as in MS2, and the other sizes come within a fraction of a pixel. Screens where 14sp is a
 * whole number of pixels (mdpi, xhdpi, xxhdpi) do not change.
 */
@Composable
fun ms2TextDensity(density: Density): Density = remember(density) {
    val px = MS2_TEXT_SIZE_SP * density.density * density.fontScale
    val roundedPx = floor(px + 0.5f)
    if (px <= 0f || roundedPx == px) density else Density(density.density, density.fontScale * roundedPx / px)
}

private const val MS2_TEXT_SIZE_SP = 14f

/**
 * Dialogs and popups get the density of their own window, without the [ms2TextDensity] of the app: their content
 * provides it again.
 */
@Composable
fun Ms2TextDensity(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalDensity provides ms2TextDensity(LocalDensity.current), content = content)
