import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class AppBottomBarState(
    val currentScreen: AppScreens,
    val isBottomBarGameItemVisible: Boolean,
)

@Composable
fun AppBottomBar(
    state: AppBottomBarState,
    navigateToOldGames: () -> Unit,
    navigateToGame: () -> Unit,
    navigateToHelp: () -> Unit,
) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary,
    ) {

        AppBottomBarItem(
            isSelected = state.currentScreen == AppScreens.OldGamesScreen,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            title = Res.string.old_games,
            onClick = navigateToOldGames
        )
        AnimatedVisibility(visible = state.isBottomBarGameItemVisible) {
            AppBottomBarItem(
                isSelected = state.currentScreen == AppScreens.GameScreen,
                selectedIcon = Icons.Filled.PlayArrow,
                unselectedIcon = Icons.Outlined.PlayArrow,
                title = Res.string.game,
                onClick = navigateToGame
            )
        }
        AppBottomBarItem(
            isSelected = state.currentScreen == AppScreens.HelpScreen,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            title = Res.string.help,
            onClick = navigateToHelp
        )
    }
}

@Composable
private fun RowScope.AppBottomBarItem(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: StringResource,
    onClick: () -> Unit
) {
    val text = with (stringResource(title)) { if (isSelected) uppercase() else this }
    val imageVector = if (isSelected) selectedIcon else unselectedIcon
    val tint = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    BottomNavigationItem(
        selected = isSelected,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(title),
                tint = tint,
            )
        },
        label = {
            Text(
                text = text,
                fontWeight = fontWeight,
                color = tint,
            )
        },
        alwaysShowLabel = true,
        onClick = onClick,
    )
}
