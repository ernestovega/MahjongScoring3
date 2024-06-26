import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.back_button
import mahjongscoring3.composeapp.generated.resources.combinations
import mahjongscoring3.composeapp.generated.resources.diffs_calculator
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import screens.game.GameScreen
import screens.old_games.OldGamesScreen

@Composable
fun App() {
    MaterialTheme {
        MahjongScoringApp()
    }
}

/**
 * enum values that represent the screens in the app
 */
enum class AppScreen(val title: StringResource) {
    //    Splash(title = Res.string.app_name),
    OldGames(title = Res.string.old_games),
    Game(title = Res.string.game),
    Combinations(title = Res.string.combinations),
    DiffsCalculator(title = Res.string.diffs_calculator)
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
            MahjongScoringAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
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
                OldGamesScreen(
                    navigateToGame = { navController.navigate(route = AppScreen.Game.name) },
                )
            }
            composable(route = AppScreen.Game.name) {
                GameScreen(

                )
            }
            composable(route = AppScreen.Combinations.name) {
//                CombinationsScreen(
//                )
            }
            composable(route = AppScreen.DiffsCalculator.name) {
//                DiffsCalculatorScreen(
//                    orderUiState = uiState,
//                    onCancelButtonClicked = { navigateToStart(navController) },
//                    onSendButtonClicked = { subject: String, summary: String ->
//                        shareOrder(subject = subject, summary = summary)
//                    },
//                    modifier = Modifier.fillMaxHeight()
//                )
            }
        }
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun MahjongScoringAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}

/**
 * Pops up to [AppScreen.OldGames]
 */
private fun navigateToStart(navController: NavHostController) {
    navController.popBackStack(AppScreen.OldGames.name, inclusive = false)
}







//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material.Button
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import org.jetbrains.compose.resources.painterResource
//import org.jetbrains.compose.ui.tooling.preview.Preview
//
//import mahjongscoring3.composeapp.generated.resources.Res
//import mahjongscoring3.composeapp.generated.resources.compose_multiplatform
//
//@Composable
//@Preview
//fun App() {
//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
//    }
//}