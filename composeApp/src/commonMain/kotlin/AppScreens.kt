import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource

enum class AppScreens(val title: StringResource) {
    //    Splash(title = Res.string.app_name),
    OldGames(title = Res.string.old_games),
    Game(title = Res.string.game),
    Help(title = Res.string.help),
}