package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.etologic.mahjongscoring3.ui.theme.Ms2TextDensity

/** A Compose [Dialog] whose texts have the MS2 sizes (see [Ms2TextDensity]). */
@Composable
fun MmDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) = Dialog(onDismissRequest = onDismissRequest, properties = properties) { Ms2TextDensity(content) }

/**
 * MS2 Widget.AppCompat.Button.Borderless.Colored with a transparent background: no padding, at least 88x48dp,
 * and the text in the center (the Compose TextButton is 64x36dp with 8dp of padding).
 */
@Composable
fun Ms2DialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minWidth: Dp = 88.dp,
    minHeight: Dp = 48.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable RowScope.() -> Unit,
) = TextButton(
    onClick = onClick,
    modifier = modifier.sizeIn(minWidth = minWidth, minHeight = minHeight),
    contentPadding = contentPadding,
    content = content,
)
