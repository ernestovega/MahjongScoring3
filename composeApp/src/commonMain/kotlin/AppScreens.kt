import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.hand_actions
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import mahjongscoring3.composeapp.generated.resources.penalty
import org.jetbrains.compose.resources.StringResource
import screens.common.ui.GameId
import screens.common.ui.SeatState
import screens.common.use_cases.utils.toJson

enum class AppScreens(
    val title: StringResource,
    val route: String
) {
    //Screens
    OldGamesScreen(title = Res.string.old_games, route = "OldGamesScreen"),
    GameScreen(title = Res.string.game, route = "GameScreen/{gameId}"),
    HelpScreen(title = Res.string.help, route = "HelpScreen"),

    //Dialogs
    CreateGameDialog(title = Res.string.create_game, route = "CreateGameDialog"),
    HandActionsDialog(title = Res.string.hand_actions, route = "HandActionsDialog/{selectedSeat}"),
    PenaltyDialog(title = Res.string.penalty, route = "PenaltyDialog/{selectedSeat}",);

    companion object {
        fun fromRoute(route: String) = when(route) {
            GameScreen.route -> GameScreen
            HelpScreen.route -> HelpScreen
            CreateGameDialog.route -> CreateGameDialog
            HandActionsDialog.route -> HandActionsDialog
            PenaltyDialog.route -> PenaltyDialog
            else -> OldGamesScreen
        }
    }
}

fun AppScreens.getRoute(gameId: GameId): String =
    route.replace(oldValue = "{gameId}", newValue = gameId.toJson(),)

fun AppScreens.getRoute(seatState: SeatState): String =
    route.replace(oldValue = "{selectedSeat}", newValue = seatState.toJson(),)