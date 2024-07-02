import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppTopBar(
    currentScreen: AppScreens,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
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
//        actions = {
//            IconButton(onClick = navigateToDiffs) {
//                Icon(
//                    imageVector = Icons.Filled.DateRange,
//                    tint = Color.White,
//                    contentDescription = stringResource(Res.string.diffs),
//                )
//            }
//            IconButton(onClick = navigateToFan) {
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    tint = Color.White,
//                    contentDescription = stringResource(Res.string.fan),
//                )
//            }
//        }
    )
}