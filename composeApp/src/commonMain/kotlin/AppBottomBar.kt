import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.game
import mahjongscoring3.composeapp.generated.resources.help
import mahjongscoring3.composeapp.generated.resources.old_games
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AppBottomBar(
    currentScreen: AppScreen,
    navigateToOldGames: () -> Unit,
    navigateToGame: () -> Unit,
    navigateToHelp: () -> Unit,
) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
    ) {
        AppBottomBarItem(
            isSelected = currentScreen.name == AppScreen.OldGames.name,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            title = Res.string.old_games,
            onClick = navigateToOldGames
        )
        AppBottomBarItem(
            isSelected = currentScreen.name == AppScreen.Game.name,
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow,
            title = Res.string.game,
            onClick = navigateToGame
        )
        AppBottomBarItem(
            isSelected = currentScreen.name == AppScreen.Help.name,
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
    BottomNavigationItem(
        selected = isSelected,
        icon = {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = stringResource(title),
                tint = if (isSelected) Color.White else Color.LightGray,
            )
        },
        label = {
            Text(
                text = stringResource(title),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        alwaysShowLabel = true,
        onClick = onClick,
    )
}
