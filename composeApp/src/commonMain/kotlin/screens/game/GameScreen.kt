package screens.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.list
import mahjongscoring3.composeapp.generated.resources.table
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Immutable
data class GameScreenState(
    val gamePageTableState: GamePageTableState = GamePageTableState(),
    val gamePageListState: GamePageListState = GamePageListState(),
)

@OptIn(ExperimentalFoundationApi::class, KoinExperimentalAPI::class)
@Composable
fun GameScreen(
    viewModel: GameScreenViewModel = koinViewModel<GameScreenViewModel>(),
) {
    val state by viewModel.screenStateFlow.collectAsState()
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    val tabTitles = listOf(
        stringResource(Res.string.table),
        stringResource(Res.string.list),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title.uppercase()) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState
        ) { page ->
            when (page) {
                0 -> GamePageTable(state.gamePageTableState) {}
                1 -> GamePageList(state.gamePageListState)
            }
        }
    }
}
