import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.etologic.mahjongscoring.App
import com.etologic.mahjongscoring.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MahjongScoring3",
        ) {
            App()
        }
    }
}