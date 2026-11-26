package com.etologic.mahjongscoring3.ui.main

import androidx.compose.foundation.Image
import com.etologic.mahjongscoring3.ui.common.actionBarMetrics
import com.etologic.mahjongscoring3.ui.common.LocalActionBarMetrics
import androidx.compose.ui.draw.shadow
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.etologic.mahjongscoring3.domain.model.NOT_SET_GAME_ID
import com.etologic.mahjongscoring3.platform.AppLocaleEnvironment
import com.etologic.mahjongscoring3.platform.LocalPlatformActionsOverride
import com.etologic.mahjongscoring3.platform.PlatformActions
import com.etologic.mahjongscoring3.platform.rememberPlatformActions
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.bg_drawer_header
import com.etologic.mahjongscoring3.resources.combinations
import com.etologic.mahjongscoring3.resources.contact_app_support
import com.etologic.mahjongscoring3.resources.contact_mahjong_madrid
import com.etologic.mahjongscoring3.resources.diffsCalculator
import com.etologic.mahjongscoring3.resources.ema_logo
import com.etologic.mahjongscoring3.resources.ema_web
import com.etologic.mahjongscoring3.resources.game
import com.etologic.mahjongscoring3.resources.green_book_en
import com.etologic.mahjongscoring3.resources.green_book_es
import com.etologic.mahjongscoring3.resources.ic_calc
import com.etologic.mahjongscoring3.resources.ic_email_black_18px
import com.etologic.mahjongscoring3.resources.ic_format_list_numbered_black_24dp
import com.etologic.mahjongscoring3.resources.ic_history_black_24px
import com.etologic.mahjongscoring3.resources.ic_import_contacts_black_24px
import com.etologic.mahjongscoring3.resources.ic_play_arrow_white_24px
import com.etologic.mahjongscoring3.resources.ic_support
import com.etologic.mahjongscoring3.resources.mahjong_madrid_web
import com.etologic.mahjongscoring3.resources.mm_logo_256
import com.etologic.mahjongscoring3.resources.no_email_apps_founded
import com.etologic.mahjongscoring3.resources.old_games
import com.etologic.mahjongscoring3.resources.press_again_to_exit
import com.etologic.mahjongscoring3.resources.send_email
import com.etologic.mahjongscoring3.ui.combinations.CombinationsScreen
import com.etologic.mahjongscoring3.ui.diffs.DiffsCalculatorScreen
import com.etologic.mahjongscoring3.ui.game.GameScreen
import com.etologic.mahjongscoring3.ui.oldgames.OldGamesScreen
import com.etologic.mahjongscoring3.ui.theme.MahjongScoringTheme
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

private const val GREEN_BOOK_ENGLISH_URL = "http://mahjong-europe.org/portal/images/docs/mcr_EN.pdf"
private const val GREEN_BOOK_SPANISH_URL = "http://mahjong-europe.org/portal/images/docs/GreenBookTranslatedintoSpanishbyIvanMaestreRos.pdf"
private const val MAHJONG_MADRID_URL = "https://www.mahjongmadrid.com"
private const val EMA_URL = "http://mahjong-europe.org/"
private const val MAHJONG_MADRID_EMAIL_ADDRESS = "mahjongmadrid@gmail.com"
private const val APP_SUPPORT_EMAIL_ADDRESS = "ernestovega85@gmail.com"
private const val LAST_BACK_PRESSED_MIN_TIME: Long = 2000

/** MS2 theme window background (Theme.AppCompat.Light, background_material_light). */
private val WINDOW_BACKGROUND = Color(0xFFFAFAFA)
private val BOTTOM_NAVIGATION_ITEM_MAX_WIDTH = 168.dp

object Routes {
    const val OLD_GAMES = "oldGames"
    const val GAME = "game"
    const val COMBINATIONS = "combinations"
    const val DIFFS_CALCULATOR = "diffsCalculator"
}

/** Shows a message at the bottom of the screen (MS2 Snackbar). */
val LocalShowSnackbar = staticCompositionLocalOf<(String) -> Unit> { {} }

/** Root of the app, shared by Android and iOS. */
@Composable
fun App() {
    MahjongScoringTheme {
        val mainViewModel = koinViewModel<MainViewModel>()
        val languageState by mainViewModel.languageState.collectAsState()
        when (val state = languageState) {
            LanguageState.Loading -> Box(Modifier.fillMaxSize().background(MmColors.GrayLight))
            is LanguageState.Loaded -> AppLocaleEnvironment(state.language) {
                MainScaffold(mainViewModel, state.language)
            }
        }
    }
}

@Composable
private fun MainScaffold(mainViewModel: MainViewModel, currentLanguage: String?) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val platformActions = LocalPlatformActionsOverride.current ?: rememberPlatformActions()
    val insets = currentAppInsets()
    val activeGameId by mainViewModel.activeGameId.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }
    val openDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }

    BoxWithConstraints(Modifier.fillMaxSize()) {
    CompositionLocalProvider(
        LocalShowSnackbar provides showSnackbar,
        LocalActionBarMetrics provides actionBarMetrics(maxWidth, maxHeight),
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier
                // MS2 pads the content on the sides, so landscape bars and notches show the window background,
                // and only the app bar grows by the status bar height, painted dark green
                .background(WINDOW_BACKGROUND)
                .windowInsetsPadding(insets.bars.only(WindowInsetsSides.Horizontal))
                // The keyboard lifts the whole content; the bottom navigation then drops its navigation bar padding
                .windowInsetsPadding(insets.ime.only(WindowInsetsSides.Bottom))
                .background(MmColors.PrimaryDark)
                .windowInsetsPadding(insets.bars.only(WindowInsetsSides.Top)),
            drawerBackgroundColor = MmColors.GrayLight,
            drawerContent = {
                Drawer(
                    platformActions = platformActions,
                    currentRoute = currentRoute,
                    isGameEnabled = activeGameId != NOT_SET_GAME_ID,
                    onNavigate = { route ->
                        closeDrawer()
                        navController.navigateTo(route)
                    },
                    onExternalAction = { closeDrawer() },
                    showSnackbar = showSnackbar,
                )
            },
            bottomBar = {
                // The tag is first so its bounds include the navigation bar padding, like MS2 bottomNavigationView
                Box(
                    Modifier.testTag("bottomNavigationView").fillMaxWidth().shadow(BottomNavigationDefaults.Elevation).background(MmColors.Primary)
                        .windowInsetsPadding(insets.bars.only(WindowInsetsSides.Bottom)),
                    contentAlignment = Alignment.TopCenter,
                ) {
                // MS2 BottomNavigationView items are at most 168dp wide and centered (tablets and landscape)
                BottomNavigation(
                    modifier = Modifier.widthIn(max = BOTTOM_NAVIGATION_ITEM_MAX_WIDTH * 3),
                    backgroundColor = MmColors.Primary,
                    contentColor = MmColors.White,
                    elevation = 0.dp,
                ) {
                    BottomItem(Routes.OLD_GAMES, painterResource(Res.drawable.ic_history_black_24px), stringResource(Res.string.old_games), currentRoute, true, navController)
                    BottomItem(Routes.GAME, painterResource(Res.drawable.ic_play_arrow_white_24px), stringResource(Res.string.game), currentRoute, activeGameId != NOT_SET_GAME_ID, navController)
                    BottomItem(Routes.COMBINATIONS, painterResource(Res.drawable.ic_format_list_numbered_black_24dp), stringResource(Res.string.combinations), currentRoute, true, navController)
                }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.OLD_GAMES,
                modifier = Modifier.padding(padding).background(MmColors.GrayLight),
            ) {
                composable(Routes.OLD_GAMES) {
                    Box(Modifier.fillMaxSize().testTag("oldGamesFragment")) {
                        PressBackTwiceToExit(platformActions)
                        OldGamesScreen(
                            viewModel = koinViewModel(),
                            platformActions = platformActions,
                            currentLanguage = currentLanguage,
                            onOpenDrawer = openDrawer,
                            onChangeLanguage = mainViewModel::changeLanguage,
                            onOpenGame = { gameId ->
                                mainViewModel.updateActiveGameId(gameId)
                                navController.navigateTo(Routes.GAME)
                            },
                        )
                    }
                }
                composable(Routes.GAME) {
                    Box(Modifier.fillMaxSize().testTag("gameFragment")) {
                        GameScreen(
                            gameId = activeGameId,
                            platformActions = platformActions,
                            onOpenDrawer = openDrawer,
                        )
                    }
                }
                composable(Routes.COMBINATIONS) {
                    Box(Modifier.fillMaxSize().testTag("combinationsFragment")) {
                        CombinationsScreen(onOpenDrawer = openDrawer)
                    }
                }
                composable(Routes.DIFFS_CALCULATOR) {
                    Box(Modifier.fillMaxSize().testTag("diffsCalculatorFragment")) {
                        DiffsCalculatorScreen(onBack = { navController.navigateUp() })
                    }
                }
            }
        }
    }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PressBackTwiceToExit(platformActions: PlatformActions) {
    var lastBackPress by remember { mutableLongStateOf(0L) }
    val showSnackbar = LocalShowSnackbar.current
    val scope = rememberCoroutineScope()
    BackHandler {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastBackPress > LAST_BACK_PRESSED_MIN_TIME) {
            lastBackPress = now
            scope.launch { showSnackbar(getString(Res.string.press_again_to_exit)) }
        } else {
            platformActions.closeApp()
        }
    }
}

/** Top level navigation keeping each screen state, like MS2 "navigateTopLevel". */
private fun NavHostController.navigateTo(route: String) {
    if (currentDestination?.route == route) return
    if (route == Routes.DIFFS_CALCULATOR) {
        navigate(route)
        return
    }
    navigate(route) {
        popUpTo(graph.startDestinationRoute ?: Routes.OLD_GAMES) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BottomItem(
    route: String,
    icon: Painter,
    label: String,
    currentRoute: String?,
    enabled: Boolean,
    navController: NavHostController,
) {
    BottomNavigationItem(
        modifier = Modifier.testTag("bottomNavigation_" + route),
        selected = currentRoute == route,
        onClick = { navController.navigateTo(route) },
        icon = { Icon(icon, contentDescription = null) },
        // MS2 BottomNavigationView: active label 14sp, inactive 12sp
        label = { Text(label, maxLines = 1, fontSize = if (currentRoute == route) 14.sp else 12.sp, fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Normal) },
        enabled = enabled,
        alwaysShowLabel = true,
        selectedContentColor = if (enabled) MmColors.White else MmColors.Gray,
        unselectedContentColor = if (enabled) MmColors.White else MmColors.Gray,
    )
}

@Composable
private fun Drawer(
    platformActions: PlatformActions,
    currentRoute: String?,
    isGameEnabled: Boolean,
    onNavigate: (String) -> Unit,
    onExternalAction: () -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val sendEmail = stringResource(Res.string.send_email)
    val noEmailApps = stringResource(Res.string.no_email_apps_founded)
    val emailSubject = "Mahjong Scoring ${platformActions.appVersionName}"
    fun contact(address: String) {
        onExternalAction()
        if (!platformActions.sendEmail(address, emailSubject, sendEmail)) showSnackbar(noEmailApps)
    }
    fun open(url: String) {
        onExternalAction()
        platformActions.openUrl(url)
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(MmDimens.DrawerHeaderHeight)) {
            Image(
                painter = painterResource(Res.drawable.bg_drawer_header),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = platformActions.appVersionName,
                color = MmColors.WhiteTransparent,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(MmDimens.HalfMargin),
            )
        }
        DrawerItem(painterResource(Res.drawable.ic_history_black_24px), stringResource(Res.string.old_games), tag = "navigationView_" + Routes.OLD_GAMES, selected = currentRoute == Routes.OLD_GAMES) { onNavigate(Routes.OLD_GAMES) }
        DrawerItem(painterResource(Res.drawable.ic_play_arrow_white_24px), stringResource(Res.string.game), tag = "navigationView_" + Routes.GAME, selected = currentRoute == Routes.GAME, enabled = isGameEnabled) { onNavigate(Routes.GAME) }
        DrawerItem(painterResource(Res.drawable.ic_format_list_numbered_black_24dp), stringResource(Res.string.combinations), tag = "navigationView_" + Routes.COMBINATIONS, selected = currentRoute == Routes.COMBINATIONS) { onNavigate(Routes.COMBINATIONS) }
        DrawerItem(painterResource(Res.drawable.ic_calc), stringResource(Res.string.diffsCalculator), tag = "navigationView_" + Routes.DIFFS_CALCULATOR, selected = currentRoute == Routes.DIFFS_CALCULATOR) { onNavigate(Routes.DIFFS_CALCULATOR) }
        DrawerItem(painterResource(Res.drawable.ic_import_contacts_black_24px), stringResource(Res.string.green_book_en)) { open(GREEN_BOOK_ENGLISH_URL) }
        DrawerItem(painterResource(Res.drawable.ic_import_contacts_black_24px), stringResource(Res.string.green_book_es)) { open(GREEN_BOOK_SPANISH_URL) }
        DrawerItem(painterResource(Res.drawable.mm_logo_256), stringResource(Res.string.mahjong_madrid_web)) { open(MAHJONG_MADRID_URL) }
        DrawerItem(painterResource(Res.drawable.ema_logo), stringResource(Res.string.ema_web)) { open(EMA_URL) }
        DrawerItem(painterResource(Res.drawable.ic_email_black_18px), stringResource(Res.string.contact_mahjong_madrid)) { contact(MAHJONG_MADRID_EMAIL_ADDRESS) }
        DrawerItem(painterResource(Res.drawable.ic_support), stringResource(Res.string.contact_app_support)) { contact(APP_SUPPORT_EMAIL_ADDRESS) }
    }
}

@Composable
private fun DrawerItem(
    icon: Painter,
    title: String,
    tag: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    tint: Boolean = true,
    onClick: () -> Unit,
) {
    val color = if (enabled) MmColors.GrayMM else MmColors.Gray
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(tag?.let { Modifier.testTag(it) } ?: Modifier)
            .height(48.dp)
            .background(if (selected) MmColors.Gray.copy(alpha = 0.3f) else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = MmDimens.StandardMargin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (tint) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        } else {
            Image(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        }
        Text(
            text = title,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 32.dp),
        )
    }
}

