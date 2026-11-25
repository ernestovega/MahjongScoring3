package com.etologic.mahjongscoring3.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.close
import com.etologic.mahjongscoring3.resources.error
import com.etologic.mahjongscoring3.resources.no
import com.etologic.mahjongscoring3.resources.ok
import com.etologic.mahjongscoring3.resources.ups_something_wrong
import com.etologic.mahjongscoring3.resources.yes
import com.etologic.mahjongscoring3.ui.theme.MmColors
import org.jetbrains.compose.resources.stringResource

/** Equivalent to MS2 "AlertDialogStyleMM" dialogs. */
@Composable
fun MmAlertDialog(
    title: String?,
    message: String?,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String?,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = MmColors.GrayLight,
        contentColor = MmColors.GrayMM,
        title = title?.let { { Text(it, fontWeight = FontWeight.Bold, fontSize = 20.sp) } },
        text = message?.let { { Text(it, fontSize = 16.sp) } },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText.uppercase(), color = MmColors.Accent)
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = onDismiss) {
                    Text(it.uppercase(), color = MmColors.Accent)
                }
            }
        },
    )
}

@Composable
fun ConfirmDialog(
    title: String?,
    message: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    MmAlertDialog(
        title = title,
        message = message,
        confirmText = stringResource(Res.string.yes),
        onConfirm = { onConfirm(); onDismiss() },
        dismissText = stringResource(Res.string.no),
        onDismiss = onDismiss,
    )
}

@Composable
fun ErrorDialog(throwable: Throwable?, onDismiss: () -> Unit) {
    MmAlertDialog(
        title = stringResource(Res.string.error),
        message = stringResource(Res.string.ups_something_wrong),
        confirmText = stringResource(Res.string.ok),
        onConfirm = onDismiss,
        dismissText = null,
        onDismiss = onDismiss,
    )
}

/** Equivalent to AlertDialog.setSingleChoiceItems + "Close" negative button. */
@Composable
fun SingleChoiceDialog(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        backgroundColor = MmColors.GrayLight,
        contentColor = MmColors.GrayMM,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(index); onDismiss() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = { onSelected(index); onDismiss() },
                            colors = RadioButtonDefaults.colors(selectedColor = MmColors.Accent),
                        )
                        Text(item, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.close).uppercase(), color = MmColors.Accent)
            }
        },
    )
}
