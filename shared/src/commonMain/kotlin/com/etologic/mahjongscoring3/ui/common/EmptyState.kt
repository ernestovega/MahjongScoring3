package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    footer: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize().background(MmColors.GrayLight),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.img_empty_box),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MmColors.Gray),
            modifier = Modifier.padding(start = MmDimens.DoubleMargin, top = topMargin, end = MmDimens.DoubleMargin),
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
