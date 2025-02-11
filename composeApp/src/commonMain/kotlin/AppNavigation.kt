import androidx.navigation.NavHostController
import domain.model.enums.TableWinds
import domain.use_cases.utils.toJson
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.hand_actions
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.hu
import mahjongscoring3.composeapp.generated.resources.old_games
import mahjongscoring3.composeapp.generated.resources.penalty
import org.jetbrains.compose.resources.StringResource
import ui.common.components.GameId

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
    HandActionsDialog(title = Res.string.hand_actions, route = "HandActionsDialog/{gameId}/{selectedSeat}"),
    HuDialog(title = Res.string.hu, route = "HuDialog/{gameId}/{selectedSeat}"),
    PenaltyDialog(title = Res.string.penalty, route = "PenaltyDialog/{gameId}/{selectedSeat}")
    ;

    companion object {
        fun fromRoute(route: String) = when(route) {
            GameScreen.route -> GameScreen
            HelpScreen.route -> HelpScreen
            CreateGameDialog.route -> CreateGameDialog
            HandActionsDialog.route -> HandActionsDialog
            HuDialog.route -> HuDialog
            PenaltyDialog.route -> PenaltyDialog
            else -> OldGamesScreen
        }
    }

    fun getRoute(gameId: GameId): String =
        route.replace(oldValue = "{gameId}", newValue = gameId.toJson())

    fun getRoute(gameId: GameId, selectedSeat: TableWinds): String =
        route.replace(oldValue = "{gameId}", newValue = gameId.toJson())
            .replace(oldValue = "{selectedSeat}", newValue = selectedSeat.toJson())
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

fun NavHostController.showHandActionsDialog(gameId: GameId, selectedSeat: TableWinds) {
    navigateTo(AppScreens.HandActionsDialog.getRoute(gameId, selectedSeat))
}

fun NavHostController.showHuDialog(gameId: GameId, selectedSeat: TableWinds) {
    navigateTo(AppScreens.HuDialog.getRoute(gameId, selectedSeat))
}

fun NavHostController.showPenaltyDialog(gameId: GameId, selectedSeat: TableWinds) {
    navigateTo(AppScreens.PenaltyDialog.getRoute(gameId, selectedSeat))
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
