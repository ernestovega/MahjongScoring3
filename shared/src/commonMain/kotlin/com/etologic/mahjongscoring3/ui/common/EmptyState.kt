package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.img_empty_box
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.painterResource

/** Gray "empty box" image with a message, as in MS2 empty layouts. */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    topMargin: androidx.compose.ui.unit.Dp = 120.dp,
    /** MS2 Old games empty layout uses android:gravity="center": the whole block is centered vertically. */
    centerVertically: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    // Change from MS2: on short screens (phones in landscape) the whole block gets smaller until it fits, instead of
    // going under the bottom navigation
    Box(
        modifier = modifier.fillMaxSize().background(MmColors.GrayLight),
        contentAlignment = if (centerVertically) Alignment.Center else Alignment.TopCenter,
    ) {
    Column(
        modifier = Modifier.scaleDownToFitHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.img_empty_box),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier
                .padding(start = MmDimens.DoubleMargin, top = topMargin, end = MmDimens.DoubleMargin)
                .size(EmptyBoxWidth, EmptyBoxHeight),
        )
        Text(
            text = message,
            color = MmColors.Gray,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.padding(start = MmDimens.DoubleMargin, top = MmDimens.StandardMargin, end = MmDimens.DoubleMargin),
        )
        footer()
    }
    }
}

/** Measures the content at its full height and, when it is taller than the space, draws it smaller from its top. */
private fun Modifier.scaleDownToFitHeight(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    val maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else placeable.height
    val scale = if (placeable.height > maxHeight && placeable.height > 0) maxHeight.toFloat() / placeable.height else 1f
    layout(placeable.width, minOf(placeable.height, maxHeight)) {
        placeable.placeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 0f)
        }
    }
}

/** Sizes of the MS2 density-specific images, in dp. */
val EmptyBoxWidth = 171.dp
val EmptyBoxHeight = 145.dp
val ArrowWidth = 168.dp
val ArrowHeight = 160.dp
