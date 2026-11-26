package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.etologic.mahjongscoring3.ui.theme.MmColors

/** Navigation icon of the toolbar: hamburger for top level screens and arrow for the rest. */
enum class TopBarNavigation { DRAWER, BACK }

/** Mahjong Scoring 2 toolbar: green, white content, elevation 4dp. */
@Composable
fun MmTopBar(
    title: String,
    navigation: TopBarNavigation,
    onNavigationClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick, modifier = Modifier.testTag("btToolbarNavigation")) {
                when (navigation) {
                    TopBarNavigation.DRAWER -> Icon(Icons.Filled.Menu, contentDescription = null)
                    TopBarNavigation.BACK -> Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = actions,
        backgroundColor = MmColors.Primary,
        contentColor = MmColors.White,
        elevation = 4.dp,
    )
}

/** [tag] is the MS2 menu item id, used by the UI tests. */
data class OverflowItem(val title: String, val tag: String? = null, val onClick: () -> Unit)

/** The "three dots" menu of the toolbar. */
@Composable
fun OverflowMenu(items: List<OverflowItem>) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }, modifier = Modifier.testTag("action_overflow")) {
        Icon(Icons.Filled.MoreVert, contentDescription = null)
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        items.forEach { item ->
            DropdownMenuItem(
                onClick = { expanded = false; item.onClick() },
                modifier = item.tag?.let { Modifier.testTag(it) } ?: Modifier,
            ) {
                Text(item.title, color = MmColors.GrayMM)
            }
        }
    }
}
