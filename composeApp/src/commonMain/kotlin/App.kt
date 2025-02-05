import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import dialogs.create_game.CreateGameDialog
import dialogs.hand_actions.HandActionsDialog
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.common.ui.NOT_SET_GAME_ID
import screens.game.GameScreen
import screens.help.HelpScreen
import screens.old_games.OldGamesScreen


@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(viewModel: AppViewModel = koinViewModel<AppViewModel>()) {
    val isDarkMode by viewModel.isDarkModeFlow.collectAsState()

    MaterialTheme(
        colors = if (isDarkMode) AppColors.DarkColorPalette else AppColors.LightColorPalette,
    ) {
        KoinContext {
            MahjongScoringApp()
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MahjongScoringApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val ongoingGameId by viewModel.ongoingGameId.collectAsState()
        val currentScreen by remember {
            derivedStateOf {
                AppScreens.fromRoute(
                    backStackEntry?.destination?.route ?: AppScreens.OldGamesScreen.route
                )
            }
        }
        val appBottomBarState by remember {
            derivedStateOf {
                AppBottomBarState(
                    currentScreen = currentScreen,
                    isBottomBarGameItemVisible = ongoingGameId != NOT_SET_GAME_ID,
                )
            }
        }

        Scaffold(
            topBar = {
                AppTopBar(
                    currentScreen = currentScreen,
                    onColorModeClick = { viewModel.changeColorMode() },
                )
            },
            bottomBar = {
                AppBottomBar(
                    state = appBottomBarState,
                    navigateToOldGames = { navController.navigateToOldGames() },
                    navigateToGame = { navController.navigateToGame(ongoingGameId) },
                    navigateToHelp = { navController.navigateToHelp() },
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppScreens.OldGamesScreen.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = AppScreens.OldGamesScreen.route) {
                    OldGamesScreen(onResumeGame = viewModel::setOngoingGameId)
                }
                composable(route = AppScreens.GameScreen.route) {
                    GameScreen(
                        onSeatClick = { selectedSeat -> navController.showHandActionsDialog(selectedSeat) },
                        onDiceClick = { navController.showDiceDialog() },
                    )
                }
                composable(route = AppScreens.HelpScreen.route) { HelpScreen() }

                dialog(route = AppScreens.CreateGameDialog.route) {
                    CreateGameDialog(
                        onDismissRequest = { navController.popBackStack() },
                        onCreateGame = viewModel::setOngoingGameId,
                    )
                }
                dialog(route = AppScreens.HandActionsDialog.route) {
                    HandActionsDialog(
                        onDismissRequest = { navController.popBackStack() },
                        onHuClick = { navController.showHuDialog(it) },
                        onPenaltyClick = { navController.showPenaltyDialog(it) },
                        onCancelPenaltyClick = { navController.showCancelPenaltiesDialog(it) },
                    )
                }
            }
        }
    }
}
