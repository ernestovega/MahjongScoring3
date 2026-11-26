package com.etologic.mahjongscoring3.ui.common

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources.blank_name_warn
import com.etologic.mahjongscoring3.resources.close
import com.etologic.mahjongscoring3.resources.double_quotes_warn
import com.etologic.mahjongscoring3.resources.game_name
import com.etologic.mahjongscoring3.resources.ic_east
import com.etologic.mahjongscoring3.resources.ic_north
import com.etologic.mahjongscoring3.resources.ic_south
import com.etologic.mahjongscoring3.resources.ic_west
import com.etologic.mahjongscoring3.resources.player_east_name
import com.etologic.mahjongscoring3.resources.player_north_name
import com.etologic.mahjongscoring3.resources.player_south_name
import com.etologic.mahjongscoring3.resources.player_west_name
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.Image

private const val MAX_GAME_NAME_LENGTH = 60
private const val MAX_PLAYER_NAME_LENGTH = 14

data class GameNames(
    val gameName: String,
    val nameP1: String,
    val nameP2: String,
    val nameP3: String,
    val nameP4: String,
)

/**
 * Port of MS2 "dialog_edit_names_fragment", used to set up a new game (button "Start")
 * and to edit the names of an existing one (button "Save").
 */
@Composable
fun NamesDialog(
    initial: GameNames,
    gameNameHint: String?,
    confirmText: String,
    onConfirm: (GameNames) -> Unit,
    onDismiss: () -> Unit,
) {
    // Saveable: what the user typed survives an activity recreation, like the MS2 EditTexts
    var gameName by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initial.gameName)) }
    var east by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initial.nameP1, TextRange(initial.nameP1.length))) }
    var south by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initial.nameP2, TextRange(initial.nameP2.length))) }
    var west by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initial.nameP3, TextRange(initial.nameP3.length))) }
    var north by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initial.nameP4, TextRange(initial.nameP4.length))) }
    val gameNameFocus = remember { FocusRequester() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(MmDimens.CardCornerRadius), color = MmColors.GrayLight, contentColor = MmColors.GrayMM) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Column(
                    Modifier
                        .padding(start = MmDimens.DoubleMargin, top = MmDimens.StandardMargin, end = MmDimens.DoubleMargin)
                        .width(230.dp)
                ) {
                    NameField(
                        value = gameName,
                        onValueChange = { if (it.text.length <= MAX_GAME_NAME_LENGTH) gameName = it },
                        label = stringResource(Res.string.game_name),
                        placeholder = gameNameHint,
                        icon = null,
                        singleLine = false,
                        tag = "tietNamesDialogGameName",
                        focusRequester = gameNameFocus,
                        bold = true,
                    )
                    Text(stringResource(Res.string.blank_name_warn), modifier = Modifier.padding(top = MmDimens.StandardMargin))
                    Spacer(Modifier.height(MmDimens.StandardAndHalfMargin))
                    NameField(east, { if (it.text.length <= MAX_PLAYER_NAME_LENGTH) east = it }, stringResource(Res.string.player_east_name), icon = Res.drawable.ic_east, tag = "tietNamesDialogEast")
                    NameField(south, { if (it.text.length <= MAX_PLAYER_NAME_LENGTH) south = it }, stringResource(Res.string.player_south_name), icon = Res.drawable.ic_south, tag = "tietNamesDialogSouth")
                    NameField(west, { if (it.text.length <= MAX_PLAYER_NAME_LENGTH) west = it }, stringResource(Res.string.player_west_name), icon = Res.drawable.ic_west, tag = "tietNamesDialogWest")
                    NameField(north, { if (it.text.length <= MAX_PLAYER_NAME_LENGTH) north = it }, stringResource(Res.string.player_north_name), icon = Res.drawable.ic_north, tag = "tietNamesDialogNorth")
                    Text(stringResource(Res.string.double_quotes_warn), modifier = Modifier.padding(top = MmDimens.StandardMargin))
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = MmDimens.HalfMargin, end = MmDimens.StandardMargin, bottom = MmDimens.HalfMargin),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = rememberSecureClick(action = onDismiss), modifier = Modifier.widthIn(min = 88.dp).testTag("btNamesDialogCancel")) {
                        Text(stringResource(Res.string.close).uppercase(), color = MmColors.GrayMM)
                    }
                    TextButton(
                        onClick = rememberSecureClick { onConfirm(GameNames(gameName.text, east.text, south.text, west.text, north.text)) },
                        modifier = Modifier.widthIn(min = 88.dp).testTag("btNamesDialogSave"),
                    ) {
                        Text(confirmText.uppercase(), color = MmColors.Accent)
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) { runCatching { gameNameFocus.requestFocus() } }
}

@Composable
private fun NameField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String? = null,
    icon: DrawableResource?,
    singleLine: Boolean = true,
    tag: String,
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
) {
    // MS2 TextInputLayout + TextInputEditText (230dp wide, 4dp background insets): small label on top,
    // drawableStart icon with 16dp padding, 18sp text, underline 3dp under the text.
    // Measured on the MS2 screenshots: label, 6dp, text line, 3dp, underline, 6dp (56dp per field).
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val align = if (bold) TextAlign.Center else TextAlign.Start
    val textStart = if (icon != null) 24.dp + MmDimens.StandardMargin else 0.dp
    Column(modifier.fillMaxWidth().padding(horizontal = MmDimens.QuarterMargin)) {
        Text(
            label,
            color = if (isFocused) MmColors.Gray else Color.Black.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = if (bold) FontWeight.Bold else null,
            fontStyle = if (bold) FontStyle.Italic else null,
            textAlign = align,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth().padding(start = textStart),
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(MmDimens.StandardMargin))
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                // MS2 EditTexts use selectAllOnFocus
                modifier = Modifier.weight(1f).testTag(tag).onFocusChanged { state ->
                    if (state.isFocused) onValueChange(value.copy(selection = TextRange(0, value.text.length)))
                }.let { if (focusRequester != null) it.focusRequester(focusRequester) else it },
                singleLine = singleLine,
                maxLines = if (singleLine) 1 else 2,
                textStyle = androidx.compose.material.LocalTextStyle.current.copy(
                    color = Color.Black.copy(alpha = 0.87f),
                    fontSize = 18.sp, // MS2 EditText default text size
                    fontWeight = if (bold) FontWeight.Bold else null,
                    fontStyle = if (bold) FontStyle.Italic else null,
                    textAlign = align,
                ),
                cursorBrush = SolidColor(MmColors.Accent),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.text.isEmpty() && placeholder != null) {
                            Text(placeholder, color = Color.Black.copy(alpha = 0.38f), fontSize = 18.sp, textAlign = align, modifier = Modifier.fillMaxWidth())
                        }
                        innerTextField()
                    }
                },
            )
        }
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier.fillMaxWidth()
                .height(if (isFocused) 2.dp else 1.dp)
                .background(if (isFocused) MmColors.Accent else Color.Black.copy(alpha = 0.42f))
        )
        Spacer(Modifier.height(if (isFocused) 5.dp else 6.dp))
    }
}
