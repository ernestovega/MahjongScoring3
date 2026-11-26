package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.ui.theme.MmColors

/**
 * MS2 toolbar size: `?android:attr/actionBarSize` and the AppCompat toolbar title size.
 * 56dp and 20sp on phones in portrait, 48dp and 14sp on phones in landscape (values-land),
 * 64dp and 20sp on tablets (values-sw600dp), where the toolbar also has 8dp of padding on each side.
 * Measured on the MS2 views in Robolectric.
 */
data class ActionBarMetrics(val height: Dp, val titleSize: TextUnit, val sidePadding: Dp = 0.dp) {
    /** For TopAppBar(windowInsets = ...): it pads the content inside the bar background. */
    val sideInsets: WindowInsets get() = WindowInsets(left = sidePadding, right = sidePadding)
}

val LocalActionBarMetrics = staticCompositionLocalOf { ActionBarMetrics(56.dp, 20.sp) }

/** [width] and [height] are the window size, like the Android configuration uses for its resource qualifiers. */
fun actionBarMetrics(width: Dp, height: Dp): ActionBarMetrics = when {
    minOf(width, height) >= 600.dp -> ActionBarMetrics(64.dp, 20.sp, sidePadding = 8.dp)
    width > height -> ActionBarMetrics(48.dp, 14.sp)
    else -> ActionBarMetrics(56.dp, 20.sp)
}

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
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = LocalActionBarMetrics.current.titleSize) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick, modifier = Modifier.testTag("btToolbarNavigation")) {
                when (navigation) {
                    TopBarNavigation.DRAWER -> Icon(Icons.Filled.Menu, contentDescription = null)
                    TopBarNavigation.BACK -> Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = actions,
        windowInsets = LocalActionBarMetrics.current.sideInsets,
        modifier = Modifier.testTag("appBarLayout").height(LocalActionBarMetrics.current.height),
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
    // MS2 OverflowMenuButton: 40dp wide, with the icon 22dp from the end of the toolbar content (the app bar
    // already adds 4dp of end padding). It also moves the action icons before it to the MS2 positions.
    Box(
        modifier = Modifier
            .offset(x = 2.dp)
            .size(width = 40.dp, height = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 20.dp),
                role = Role.Button,
                onClick = { expanded = true },
            )
            .testTag("action_overflow"),
        contentAlignment = Alignment.Center,
    ) {
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
