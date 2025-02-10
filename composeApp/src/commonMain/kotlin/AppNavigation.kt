import androidx.navigation.NavHostController
import domain.use_cases.utils.toJson
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.hand_actions
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import mahjongscoring3.composeapp.generated.resources.penalty
import org.jetbrains.compose.resources.StringResource
import ui.common.components.GameId
import ui.common.components.SeatState

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

    fun getRoute(gameId: GameId): String =
        route.replace(
            oldValue = "{gameId}",
            newValue = gameId.toJson(),
        )

    fun getRoute(seatState: SeatState): String =
        route.replace(
            oldValue = "{selectedSeat}",
            newValue = seatState.toJson(),
        )
}

fun NavHostController.navigateToOldGames() {
    navigateTo(AppScreens.OldGamesScreen.route)
}

fun NavHostController.navigateToGame(gameId: GameId) {
    navigateTo(AppScreens.GameScreen.getRoute(gameId))
}

fun NavHostController.navigateToHelp() {
    navigateTo(AppScreens.HelpScreen.route)
}

fun NavHostController.showCreateGameDialog() {
    navigateTo(AppScreens.CreateGameDialog.route)
}

fun NavHostController.showDiceDialog() {
//    navigateTo(AppScreens.DiceDialog)
}

fun NavHostController.showHandActionsDialog(selectedSeat: SeatState) {
    navigateTo(AppScreens.HandActionsDialog.getRoute(selectedSeat))
}

fun NavHostController.showHuDialog(selectedSeat: SeatState) {
//    navigateTo(AppScreens.HuDialog, args = selectedSeat)
}

fun NavHostController.showPenaltyDialog(selectedSeat: SeatState) {
    navigateTo(AppScreens.PenaltyDialog.getRoute(selectedSeat))
}

private fun NavHostController.navigateTo(
    route: String,
    inclusive: Boolean = false,
) {
    if (currentBackStackEntry?.destination?.route != route) {
        if (!popBackStack(route, inclusive = inclusive)) {
            navigate(route)
        }
    }
}

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}
