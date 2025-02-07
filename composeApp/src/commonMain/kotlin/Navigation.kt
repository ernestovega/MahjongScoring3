import androidx.navigation.NavHostController
import screens.common.ui.GameId
import screens.common.ui.SeatState

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