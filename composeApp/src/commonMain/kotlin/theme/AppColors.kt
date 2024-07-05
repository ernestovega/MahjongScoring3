package theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import theme.AppColors.DarkBackground
import theme.AppColors.DarkError
import theme.AppColors.DarkOnBackground
import theme.AppColors.DarkOnError
import theme.AppColors.DarkOnPrimary
import theme.AppColors.DarkOnSecondary
import theme.AppColors.DarkOnSurface
import theme.AppColors.DarkPrimary
import theme.AppColors.DarkPrimaryVariant
import theme.AppColors.DarkSecondary
import theme.AppColors.DarkSecondaryVariant
import theme.AppColors.DarkSurface
import theme.AppColors.LightBackground
import theme.AppColors.LightError
import theme.AppColors.LightOnBackground
import theme.AppColors.LightOnError
import theme.AppColors.LightOnPrimary
import theme.AppColors.LightOnSecondary
import theme.AppColors.LightOnSurface
import theme.AppColors.LightPrimary
import theme.AppColors.LightPrimaryVariant
import theme.AppColors.LightSecondary
import theme.AppColors.LightSecondaryVariant
import theme.AppColors.LightSurface

object AppColors {
    val greenMM = Color(0xFF00B16A)
    val greenMMDark = Color(0xFF007B49)
    val pink = Color(0xFFFF4E95)
    val purple = Color(0xFF7C00E7)
    val red = Color(0xFFFF0000)
    val golden = Color(0xFFFFC107)

    val white = Color(0xFFFFFFFF)
    val grayLight = Color(0xFFEEEEEE)
    val grayBg = Color(0xFFBBBBBB)
    val grayMedium = Color(0xFF888888)
    val grayDark = Color(0xFF414141)
    val black = Color(0xFF000000)

    val dark_greenMM = Color(0xFF007B49)
    val dark_greenMMDark = Color(0xFF00502F)
    val dark_pink = Color(0xFFAA3564)
    val dark_purple = Color(0xFF450080)
    val dark_red = Color(0xFF9C0000)
    val dark_grayBg = Color(0xFF222222)

    // Light Theme Colors
    val LightPrimary = greenMM
    val LightPrimaryVariant = greenMMDark
    val LightSecondary = pink
    val LightSecondaryVariant = purple
    val LightBackground = grayBg
    val LightSurface = grayLight
    val LightError = red
    val LightOnPrimary = white
    val LightOnSecondary = white
    val LightOnBackground = black
    val LightOnSurface = grayDark
    val LightOnError = grayLight

    // Dark Theme Colors
    val DarkPrimary = dark_greenMM
    val DarkPrimaryVariant = dark_greenMMDark
    val DarkSecondary = dark_pink
    val DarkSecondaryVariant = dark_purple
    val DarkBackground = dark_grayBg
    val DarkSurface = grayDark
    val DarkError = dark_red
    val DarkOnPrimary = white
    val DarkOnSecondary = grayLight
    val DarkOnBackground = white
    val DarkOnSurface = white
    val DarkOnError = white
}

val LightColorPalette = lightColors(
    primary = LightPrimary,
    primaryVariant = LightPrimaryVariant,
    secondary = LightSecondary,
    secondaryVariant = LightSecondaryVariant,
    background = LightBackground,
    surface = LightSurface,
    error = LightError,
    onPrimary = LightOnPrimary,
    onSecondary = LightOnSecondary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onError = LightOnError,
)

val DarkColorPalette = darkColors(
    primary = DarkPrimary,
    primaryVariant = DarkPrimaryVariant,
    secondary = DarkSecondary,
    secondaryVariant = DarkSecondaryVariant,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onError = DarkOnError,
)