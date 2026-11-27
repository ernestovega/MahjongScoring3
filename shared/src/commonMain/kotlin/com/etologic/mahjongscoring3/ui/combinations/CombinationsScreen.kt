package com.etologic.mahjongscoring3.ui.combinations

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.arrow
import com.etologic.mahjongscoring3.resources.combination_name
import com.etologic.mahjongscoring3.resources.combinations
import com.etologic.mahjongscoring3.resources.ic_books
import com.etologic.mahjongscoring3.resources.ic_photos
import com.etologic.mahjongscoring3.resources.ic_search
import com.etologic.mahjongscoring3.resources.img_empty_box
import com.etologic.mahjongscoring3.resources.search
import com.etologic.mahjongscoring3.resources.there_arent_combinations_try_new_search
import com.etologic.mahjongscoring3.resources.toggle_descriptions
import com.etologic.mahjongscoring3.ui.common.ArrowHeight
import com.etologic.mahjongscoring3.ui.common.ArrowWidth
import com.etologic.mahjongscoring3.ui.common.EmptyBoxHeight
import com.etologic.mahjongscoring3.ui.common.EmptyBoxWidth
import com.etologic.mahjongscoring3.ui.common.LocalActionBarMetrics
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.etologic.mahjongscoring3.ui.common.rememberSecureClick

internal data class ResolvedCombination(
    val combination: Combination,
    val name: String,
    val description: String?,
)

/** Same filter as MS2 CombinationsViewModel: by localized name, description or points, ignoring case. */
internal fun filterCombinations(combinations: List<ResolvedCombination>, filter: String): List<ResolvedCombination> =
    combinations.filter {
        it.name.contains(filter, ignoreCase = true) ||
            it.description?.contains(filter, ignoreCase = true) == true ||
            it.combination.points.toString().contains(filter, ignoreCase = true)
    }

@Composable
fun CombinationsScreen(onOpenDrawer: () -> Unit) {
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var filter by rememberSaveable { mutableStateOf("") }
    var showAllDetails by rememberSaveable { mutableStateOf(false) }
    // Items toggled individually; reset when the global toggle changes (like MS2 adapter)
    val toggledItems = remember(showAllDetails) { mutableStateMapOf<Int, Boolean>() }

    val resolved = allCombinations.map { combination ->
        ResolvedCombination(
            combination = combination,
            name = stringResource(combination.name),
            description = combination.description?.let { stringResource(it) },
        )
    }
    val filtered = filterCombinations(resolved, filter)

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.testTag("appBarLayout").height(LocalActionBarMetrics.current.height),
            windowInsets = LocalActionBarMetrics.current.sideInsets,
            backgroundColor = MmColors.Primary,
            contentColor = MmColors.White,
            elevation = 4.dp,
            navigationIcon = {
                IconButton(onClick = {
                    if (isSearching) {
                        isSearching = false
                        filter = ""
                    } else {
                        onOpenDrawer()
                    }
                }) {
                    if (isSearching) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    } else {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
            },
            title = {
                if (isSearching) {
                    val focusRequester = remember { FocusRequester() }
                    // A text field without the 56dp of the Material TextField: the app bar is 48dp high on landscape
                    // phones (like MS2), and the text stays centered in it, like the MS2 SearchView
                    BasicTextField(
                        value = filter,
                        onValueChange = { filter = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.subtitle1.copy(color = MmColors.White),
                        cursorBrush = SolidColor(MmColors.White),
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester).testTag("etCombinationsSearch"),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.weight(1f)) {
                                    if (filter.isEmpty()) {
                                        Text(stringResource(Res.string.combination_name), color = MmColors.WhiteTransparent, maxLines = 1)
                                    }
                                    innerTextField()
                                }
                                if (filter.isNotEmpty()) {
                                    IconButton(onClick = { filter = "" }) { Icon(Icons.Filled.Close, null, tint = MmColors.White) }
                                }
                            }
                        },
                    )
                    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
                } else {
                    Text(stringResource(Res.string.combinations), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = LocalActionBarMetrics.current.titleSize)
                }
            },
            actions = {
                if (!isSearching) {
                    IconButton(onClick = { isSearching = true }, modifier = Modifier.testTag("action_search")) {
                        Icon(painterResource(Res.drawable.ic_search), contentDescription = stringResource(Res.string.search), tint = MmColors.White)
                    }
                }
                IconButton(onClick = { showAllDetails = !showAllDetails }) {
                    Icon(
                        painter = painterResource(if (showAllDetails) Res.drawable.ic_books else Res.drawable.ic_photos),
                        contentDescription = stringResource(Res.string.toggle_descriptions),
                        tint = MmColors.White,
                    )
                }
            },
        )
        Box(Modifier.fillMaxSize()) {
            if (filtered.isEmpty()) {
                CombinationsEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(MmColors.Gray),
                    contentPadding = PaddingValues(vertical = MmDimens.QuarterMargin),
                ) {
                    itemsIndexed(filtered, key = { _, item -> item.combination.position }) { index, item ->
                        val position = item.combination.position
                        CombinationItem(
                            item = item,
                            numberInList = index + 1,
                            isExpanded = toggledItems[position] ?: showAllDetails,
                            onClick = { toggledItems[position] = !(toggledItems[position] ?: showAllDetails) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CombinationItem(
    item: ResolvedCombination,
    numberInList: Int,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MmDimens.HalfMargin, vertical = MmDimens.QuarterMargin)
            .defaultMinSize(minHeight = 48.dp)
            .clickable(onClick = rememberSecureClick(action = onClick)),
        backgroundColor = MmColors.GrayLight,
        shape = RoundedCornerShape(MmDimens.CardCornerRadius),
    ) {
        Column(
            Modifier
                .animateContentSize()
                .padding(vertical = MmDimens.HalfMargin)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = MmDimens.StandardMargin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.combination.points.toString(),
                    color = Color.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.defaultMinSize(minWidth = MmDimens.DoubleMargin),
                )
                Text(
                    text = item.name,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "#$numberInList",
                    color = MmColors.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = MmDimens.HalfMargin),
                )
            }
            if (isExpanded) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(horizontal = MmDimens.StandardMargin)
                        .padding(top = 0.dp)
                ) {
                    val image = item.combination.image
                    if (image != null) {
                        Image(
                            painter = painterResource(image),
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Text(
                            text = item.description.orEmpty(),
                            color = MmColors.GrayMM,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.CenterStart),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CombinationsEmptyState() {
    Column(Modifier.fillMaxSize().background(MmColors.GrayLight)) {
        Image(
            painter = painterResource(Res.drawable.arrow),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier
                .padding(start = MmDimens.QuadrupleMargin, top = MmDimens.DoubleMargin, end = MmDimens.DoubleMargin, bottom = MmDimens.DoubleMargin)
                .size(ArrowWidth, ArrowHeight)
                .graphicsLayer(rotationZ = -90f, rotationY = 180f),
        )
        Image(
            painter = painterResource(Res.drawable.img_empty_box),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = MmDimens.DoubleMargin, top = MmDimens.StandardMargin, end = MmDimens.DoubleMargin)
                .size(EmptyBoxWidth, EmptyBoxHeight),
        )
        Text(
            text = stringResource(Res.string.there_arent_combinations_try_new_search),
            color = MmColors.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = MmDimens.DoubleMargin, top = MmDimens.DoubleMargin, end = MmDimens.DoubleMargin),
        )
    }
}
