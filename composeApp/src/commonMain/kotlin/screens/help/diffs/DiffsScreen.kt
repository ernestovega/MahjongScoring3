package screens.help.diffs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

data class DiffsScreenState(
    val diffs: List<Diff> = emptyList(),
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun DiffsScreen(
    viewModel: DiffsScreenViewModel = koinViewModel<DiffsScreenViewModel>(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.screenStateFlow.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect {
                viewModel.loadNextInterval()
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        DiffsScreenHeader()

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
        ) {
            itemsIndexed(state.diffs) { index, item ->
                DiffsScreenItem(state = item)
                if (index < state.diffs.size - 1) {
                    Divider()
                }
            }
        }
    }
}

