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
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource
import screens.game.GameScreen
import screens.help.HelpScreen
import screens.old_games.OldGamesScreen

@Composable
fun App() {
    MaterialTheme {
        MahjongScoringApp()
    }
}

enum class AppScreen(val title: StringResource) {
    //    Splash(title = Res.string.app_name),
    OldGames(title = Res.string.old_games),
    Game(title = Res.string.game),
    Help(title = Res.string.help),
}

@Composable
fun MahjongScoringApp(
//    viewModel: OrderViewModel = viewModel { OrderViewModel() },
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(backStackEntry?.destination?.route ?: AppScreen.OldGames.name)

    Scaffold(
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
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
            startDestination = AppScreen.OldGames.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.OldGames.name) {
                OldGamesScreen(navigateToGame = { navController.navigateToGame() })
            }
            composable(route = AppScreen.Game.name) {
                GameScreen()
            }
            composable(route = AppScreen.Help.name) {
                HelpScreen()
            }
        }
    }
}

private fun NavHostController.navigateToOldGames() {
    if (currentBackStackEntry?.destination?.route != AppScreen.OldGames.name) {
        if (!popBackStack(AppScreen.OldGames.name, inclusive = false)) {
            navigate(route = AppScreen.OldGames.name)
        }
    }
}

private fun NavHostController.navigateToGame() {
    if (currentBackStackEntry?.destination?.route != AppScreen.Game.name) {
        if (!popBackStack(AppScreen.Game.name, inclusive = false)) {
            navigate(route = AppScreen.Game.name)
        }
    }
}

private fun NavHostController.navigateToHelp() {
    if (currentBackStackEntry?.destination?.route != AppScreen.Help.name) {
        if (!popBackStack(AppScreen.Help.name, inclusive = false)) {
            navigate(route = AppScreen.Help.name)
        }
    }
}

///**
// * Pops up to [AppScreen.OldGames]
// */
//private fun NavHostController.navigateToStart() {
//    popBackStack(AppScreen.OldGames.name, inclusive = false)
//}
