package screens.help

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import mahjongscoring3.composeapp.generated.resources.Res
import mahjongscoring3.composeapp.generated.resources.diffs
import mahjongscoring3.composeapp.generated.resources.fan
import mahjongscoring3.composeapp.generated.resources.penalties
import mahjongscoring3.composeapp.generated.resources.rules
import org.jetbrains.compose.resources.stringResource
import screens.help.diffs.DiffsScreen
import screens.help.fan.FanScreen
import screens.help.penalties.PenaltiesScreen
import screens.help.rules.RulesScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HelpScreen() {
    val tabTitles = listOf(
        stringResource(Res.string.fan),
        stringResource(Res.string.diffs),
        stringResource(Res.string.penalties),
        stringResource(Res.string.rules),
    )
    val pagerState = rememberPagerState { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selected = isSelected,
                    onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } }
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> FanScreen()
                1 -> DiffsScreen()
                2 -> PenaltiesScreen()
                3 -> RulesScreen()
            }
        }
    }
}
