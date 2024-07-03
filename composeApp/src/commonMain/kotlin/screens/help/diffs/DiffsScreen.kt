package screens.help.diffs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.diff
import mahjongscoring3.composeapp.generated.resources.direct
import mahjongscoring3.composeapp.generated.resources.indirect
import mahjongscoring3.composeapp.generated.resources.self_pick
import org.jetbrains.compose.resources.stringResource
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
            .background(MaterialTheme.colors.background),
    ) {
        DiffsScreenHeader()

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState,
        ) {
            items(state.diffs) { item ->
                DiffsScreenItem(state = item)
            }
        }
    }
}

@Composable
private fun DiffsScreenHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(
            Res.string.diff,
            Res.string.self_pick,
            Res.string.direct,
            Res.string.indirect,
        ).forEach { title ->
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(title),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}
