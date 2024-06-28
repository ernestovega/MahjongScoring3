package screens.diffs_calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DiffsCalculatorScreen(
    modifier: Modifier = Modifier,
) {
    val combinations by remember { mutableStateOf(mutableListOf<Int>().apply { for (i in 1..200) add(i) }.toList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        ) {
        DiffsCalculatorItem(
            diffsCalculatorItemState = 0,
            backgroundColor = MaterialTheme.colors.primary,
            textColor = Color.White,
            height = 40.dp,
        )

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(combinations) { item ->
                DiffsCalculatorItem(diffsCalculatorItemState = item)
            }
        }
    }
}
