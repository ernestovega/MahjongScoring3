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
import androidx.navigation.compose.rememberNavController
import dialogs.create_game.CreateGameDialog
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
    val currentScreen = AppScreens.valueOf(backStackEntry?.destination?.route ?: AppScreens.OldGames.name)
    val ongoingGameId by viewModel.ongoingGameId.collectAsState()
    val appBottomBarState by remember {
        derivedStateOf {
            AppBottomBarState(
                isBottomBarGameItemVisible = ongoingGameId != NOT_SET_GAME_ID,
                currentScreen = currentScreen,
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
                navigateToGame = { navController.navigateToGame() },
                navigateToHelp = { navController.navigateToHelp() },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.OldGames.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screens
            composable(route = AppScreens.OldGames.name) {
                OldGamesScreen(
                    navigateToGame = { gameId ->
                        viewModel.setOngoingGameId(gameId)
                        navController.navigateToGame()
                    },
                    openCreateGameDialog = { navController.showCreateGameDialog() },
                )
            }
            composable(route = AppScreens.Game.name) {
                GameScreen(ongoingGameId)
            }
            composable(route = AppScreens.Help.name) {
                HelpScreen()
            }

            // Dialogs
            composable(route = AppDialogs.CreateGameDialog.name) {
                CreateGameDialog(
                    onDismissRequest = { navController.popBackStack() },
                    navigateToGame = { gameId ->
                        viewModel.setOngoingGameId(gameId)
                        navController.navigateToGame()
                    },
                )
            }
        }
    }
}

private fun NavHostController.navigateToOldGames() {
    if (currentBackStackEntry?.destination?.route != AppScreens.OldGames.name) {
        if (!popBackStack(AppScreens.OldGames.name, inclusive = false)) {
            navigate(route = AppScreens.OldGames.name)
        }
    }
}

private fun NavHostController.navigateToGame() {
    if (currentBackStackEntry?.destination?.route != AppScreens.Game.name) {
        if (!popBackStack(AppScreens.Game.name, inclusive = false)) {
            navigate(route = AppScreens.Game.name)
        }
    }
}

private fun NavHostController.navigateToHelp() {
    if (currentBackStackEntry?.destination?.route != AppScreens.Help.name) {
        if (!popBackStack(AppScreens.Help.name, inclusive = false)) {
            navigate(route = AppScreens.Help.name)
        }
    }
}

private fun NavHostController.showCreateGameDialog() {
    if (currentBackStackEntry?.destination?.route != AppDialogs.CreateGameDialog.name) {
        if (!popBackStack(AppDialogs.CreateGameDialog.name, inclusive = false)) {
            navigate(route = AppDialogs.CreateGameDialog.name)
        }
    }
}

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}
