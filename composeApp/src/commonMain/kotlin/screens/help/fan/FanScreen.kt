package screens.help.fan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Immutable
data class FanScreenState(
    val fan: List<Fan> = emptyList(),
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun FanScreen(
    viewModel: FanScreenViewModel = koinViewModel<FanScreenViewModel>(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.screenStateFlow.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(state.fan) { itemState ->
            FanScreenItem(
                state = itemState,
                onClick = {},
            )
        }
    }
}
