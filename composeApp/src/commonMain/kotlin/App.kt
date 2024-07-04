import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import screens.game.GameScreen
import screens.help.HelpScreen
import screens.old_games.OldGamesScreen

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            MahjongScoringApp()
        }
    }
}

@Composable
fun MahjongScoringApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreens.valueOf(backStackEntry?.destination?.route ?: AppScreens.OldGames.name)

    Scaffold(
        topBar = {
            AppTopBar(currentScreen = currentScreen,)
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
            composable(route = AppScreens.OldGames.name) {
                OldGamesScreen(navigateToGame = { navController.navigateToGame() })
            }
            composable(route = AppScreens.Game.name) {
                GameScreen()
            }
            composable(route = AppScreens.Help.name) {
                HelpScreen()
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

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}
