import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.create_game
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.hand_actions
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource
import screens.common.ui.SeatState
import screens.common.use_cases.utils.toJson

sealed class AppScreens(
    val title: StringResource,
    val route: String,
    vararg val params: String,
) {
    open fun <T> route(vararg args: T): String = route

    data object OldGamesScreen : AppScreens(
        title = Res.string.old_games,
        route = "OldGamesScreen",
    )

    data object GameScreen : AppScreens(
        title = Res.string.game,
        route = "GameScreen/{gameId}",
        params = arrayOf("{gameId}")
    ) {
        override fun <T> route(vararg args: T): String =
            route.replace(
                oldValue = params.first(),
                newValue = (args.first() as Long).toJson(),
            )
    }

    data object HelpScreen : AppScreens(
        title = Res.string.help,
        route = "HelpScreen",
    )

    data object CreateGameDialog : AppScreens(
        title = Res.string.create_game,
        route = "CreateGameDialog",
    )

    data object HandActionsDialog : AppScreens(
        title = Res.string.hand_actions,
        route = "HandActionsDialog/{selectedSeat}",
        params = arrayOf("{selectedSeat}"),
    ) {
        override fun <T> route(vararg args: T): String =
            route.replace(
                oldValue = params.first(),
                newValue = (args.first() as SeatState).toJson(),
            )
    }

    companion object {
        fun fromRoute(route: String) = when(route) {
            GameScreen.route -> GameScreen
            HelpScreen.route -> HelpScreen
            CreateGameDialog.route -> CreateGameDialog
            HandActionsDialog.route -> HandActionsDialog
            else -> OldGamesScreen
        }

    }
}