import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

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

    val LightColorPalette = lightColors(
        primary = greenMM,
        primaryVariant = greenMMDark,
        secondary = pink,
        secondaryVariant = purple,
        background = grayBg,
        surface = grayLight,
        error = red,
        onPrimary = white,
        onSecondary = white,
        onBackground = black,
        onSurface = grayDark,
        onError = grayLight,
    )

    val DarkColorPalette = darkColors(
        primary = dark_greenMM,
        primaryVariant = dark_greenMMDark,
        secondary = dark_pink,
        secondaryVariant = dark_purple,
        background = dark_grayBg,
        surface = grayDark,
        error = dark_red,
        onPrimary = white,
        onSecondary = grayLight,
        onBackground = white,
        onSurface = white,
        onError = white,
    )
}
