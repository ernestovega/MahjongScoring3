import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.hand_actions
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource

enum class AppScreens(val title: StringResource) {
    //    Splash(title = Res.string.app_name),
    OldGamesScreen(title = Res.string.old_games),
    GameScreen(title = Res.string.game),
    HelpScreen(title = Res.string.help),

    CreateGameDialog(title = Res.string.create_game),
    HandActionsDialog(title = Res.string.hand_actions),
}
