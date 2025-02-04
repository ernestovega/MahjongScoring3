import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun App(
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
) {
    val isDarkMode by viewModel.isDarkModeFlow.collectAsState()

    MaterialTheme(
        colors = if (isDarkMode) AppColors.DarkColorPalette else AppColors.LightColorPalette,
    ) {
        KoinContext {
            MahjongScoringApp()
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MahjongScoringApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = koinViewModel<AppViewModel>(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ongoingGameId by viewModel.ongoingGameId.collectAsState()
    val currentScreen by remember {
        derivedStateOf {
            AppScreens.valueOf(
                backStackEntry?.destination?.route ?: AppScreens.OldGamesScreen.name
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
    val selectedSeatState by viewModel.selectedSeatState.collectAsState()

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
                navigateToOldGames = { navController.navigateTo(AppScreens.OldGamesScreen) },
                navigateToGame = { navController.navigateTo(AppScreens.GameScreen) },
                navigateToHelp = { navController.navigateTo(AppScreens.HelpScreen) },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.OldGamesScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreens.OldGamesScreen.name) {
                OldGamesScreen(
                    navigateToGame = { gameId ->
                        viewModel.setOngoingGameId(gameId)
                        navController.navigateTo(AppScreens.GameScreen)
                    },
                    openCreateGameDialog = { navController.navigateTo(AppScreens.CreateGameDialog) },
                )
            }
            composable(route = AppScreens.GameScreen.name) {
                GameScreen(
                    gameId = ongoingGameId,
                    openHandActionsDialog = { selectedSeatState ->
                        viewModel.setSelectedSeat(selectedSeatState)
                        navController.navigateTo(AppScreens.HandActionsDialog)
                    },
                )
            }
            composable(route = AppScreens.HelpScreen.name) {
                HelpScreen()
            }

            dialog(route = AppScreens.CreateGameDialog.name) {
                CreateGameDialog(
                    onDismissRequest = { navController.popBackStack() },
                    navigateToGame = { gameId ->
                        viewModel.setOngoingGameId(gameId)
                        navController.navigateTo(AppScreens.GameScreen)
                    },
                )
            }
            dialog(route = AppScreens.HandActionsDialog.name) {
                selectedSeatState?.let { it1 ->
                    HandActionsDialog(
                        selectedSeat = it1,
                        onDismissRequest = { navController.popBackStack() },
                        navigateToHuDialog = { selectedPlayerData -> },
                        navigateToPenaltyDialog = { selectedPlayerData -> },
                        navigateToCancelPenaltyDialog = { selectedPlayerData -> },
                    )
                }
            }
        }
    }
}

private fun NavHostController.navigateTo(screen: AppScreens, inclusive: Boolean = false) {
    if (currentBackStackEntry?.destination?.route != screen.name) {
        if (!popBackStack(screen.name, inclusive = inclusive)) {
            navigate(route = screen.name)
        }
    }
}

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}
