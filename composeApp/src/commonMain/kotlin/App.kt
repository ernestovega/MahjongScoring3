import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import screens.game.GameScreen
import screens.help.HelpScreen
import dialogs.CreateGameDialog
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
            MahjongScoringApp(
                changeColorMode = { viewModel.changeColorMode() }
            )
        }
    }
}

@Composable
fun MahjongScoringApp(
    navController: NavHostController = rememberNavController(),
    changeColorMode: () -> Unit,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreens.valueOf(backStackEntry?.destination?.route ?: AppScreens.OldGames.name)

    Scaffold(
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                onColorModeClick = changeColorMode,
            )
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = currentScreen,
                navigateToOldGames = { navController.navigateToOldGames() },
                navigateToGame = { navController.navigateToGame() },
                navigateToHelp = { navController.navigateToHelp() },
            )
        }
    ) { innerPadding ->
//        val uiState by viewModel.uiState.collectAsState()

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
                    navigateToGame = { navController.navigateToGame() },
                    openCreateGameDialog = { navController.showCreateGameDialog() },
                )
            }
            composable(route = AppScreens.Game.name) {
                GameScreen()
            }
            composable(route = AppScreens.Help.name) {
                HelpScreen()
            }

            // Dialogs
            composable(route = AppDialogs.CreateGameDialog.name) {
                CreateGameDialog(
                    onDismissRequest = { navController.popBackStack() },
                    navigateToGame = { navController.navigateToGame() },
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
