import androidx.navigation.NavHostController
import screens.common.ui.GameId
import screens.common.ui.SeatState

fun NavHostController.navigateToOldGames() {
    navigateTo(AppScreens.OldGamesScreen)
}

fun NavHostController.navigateToGame(gameId: GameId) {
    navigateTo(AppScreens.GameScreen, args = gameId)
}

fun NavHostController.navigateToHelp() {
    navigateTo(AppScreens.HelpScreen)
}

fun NavHostController.showCreateGameDialog() {
    navigateTo(AppScreens.CreateGameDialog)
}

fun NavHostController.showDiceDialog() {
//    navigateTo(AppScreens.DiceDialog)
}

fun NavHostController.showHandActionsDialog(selectedSeat: SeatState) {
    navigateTo(AppScreens.HandActionsDialog, args = selectedSeat)
}

fun NavHostController.showHuDialog(selectedSeat: SeatState) {
//    navigateTo(AppScreens.HuDialog, args = selectedSeat)
}

fun NavHostController.showPenaltyDialog(selectedSeat: SeatState) {
//    navigateTo(AppScreens.PenaltyDialog, args = selectedSeat)
}

private fun NavHostController.navigateTo(
    screen: AppScreens,
    inclusive: Boolean = false,
) {
    if (currentBackStackEntry?.destination?.route != screen.route) {
        if (!popBackStack(screen.route, inclusive = inclusive)) {
            navigate(screen.route)
        }
    }
}

private inline fun <reified T> NavHostController.navigateTo(
    screen: AppScreens,
    inclusive: Boolean = false,
    args: T? = null,
) {
    if (currentBackStackEntry?.destination?.route != screen.route) {
        if (!popBackStack(screen.route, inclusive = inclusive)) {
            navigate(screen.route(args))
        }
    }
}

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}