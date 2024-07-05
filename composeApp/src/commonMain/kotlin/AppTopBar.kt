import androidx.compose.foundation.background
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.color_mode
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    currentScreen: AppScreens,
    modifier: Modifier = Modifier,
    onColorModeClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        contentColor = MaterialTheme.colors.onPrimary,
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(stringResource(currentScreen.title)) },
//        colors = TopAppBarDefaults.mediumTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        ),
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = stringResource(Res.string.back_button)
//                    )
//                }
//            }
//        },
        actions = {
            IconButton(onClick = onColorModeClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = stringResource(Res.string.color_mode),
                )
            }
        }
    )
}