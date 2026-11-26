package com.etologic.mahjongscoring3.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etologic.mahjongscoring3.domain.model.PointsDiff
import com.etologic.mahjongscoring3.domain.model.SeatDiffs
import com.etologic.mahjongscoring3.domain.model.TableWinds
import com.etologic.mahjongscoring3.domain.utils.toStringOrHyphen
import com.etologic.mahjongscoring3.resources.Res
import com.etologic.mahjongscoring3.resources._1st
import com.etologic.mahjongscoring3.resources._2nd
import com.etologic.mahjongscoring3.resources._3rd
import com.etologic.mahjongscoring3.resources.backspace
import com.etologic.mahjongscoring3.resources.direct
import com.etologic.mahjongscoring3.resources.direct_abr
import com.etologic.mahjongscoring3.resources.ic_backspace_white_24dp
import com.etologic.mahjongscoring3.resources.ic_east
import com.etologic.mahjongscoring3.resources.ic_north
import com.etologic.mahjongscoring3.resources.ic_south
import com.etologic.mahjongscoring3.resources.ic_west
import com.etologic.mahjongscoring3.resources.indirect
import com.etologic.mahjongscoring3.resources.indirect_abr
import com.etologic.mahjongscoring3.resources.self_abr
import com.etologic.mahjongscoring3.resources.self_pick
import com.etologic.mahjongscoring3.resources.to_be_by
import com.etologic.mahjongscoring3.ui.theme.MmColors
import com.etologic.mahjongscoring3.ui.theme.MmDimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** MS2 seat states: NORMAL (grayMM), SELECTED (accent), DISABLED (gray, game ended). */
enum class SeatState(val color: Color) {
    NORMAL(MmColors.GrayMM),
    SELECTED(MmColors.Accent),
    DISABLED(MmColors.Gray),
}

fun TableWinds.icon(): DrawableResource? =
    when (this) {
        TableWinds.EAST -> Res.drawable.ic_east
        TableWinds.SOUTH -> Res.drawable.ic_south
        TableWinds.WEST -> Res.drawable.ic_west
        TableWinds.NORTH -> Res.drawable.ic_north
        TableWinds.NONE -> null
    }

/** Wind icon of a seat, 24dp, tinted. */
@Composable
fun WindIcon(wind: TableWinds, tint: Color?, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 24.dp) {
    val icon = wind.icon()
    if (icon == null) {
        Spacer(modifier.size(size))
    } else {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = tint?.let { ColorFilter.tint(it) },
            modifier = modifier.size(size),
        )
    }
}

/** MS2 "custom_table_seat_medium": wind icon and player name. */
@Composable
fun SeatMedium(wind: TableWinds, name: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(MmDimens.HalfMargin), horizontalAlignment = Alignment.CenterHorizontally) {
        WindIcon(wind, MmColors.GrayMM, size = MmDimens.StandardAndHalfMargin)
        Text(name, color = MmColors.GrayMM, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

/** MS2 "custom_diffs_small": points needed to be 1st/2nd/3rd by self pick, direct and indirect hu. */
@Composable
fun DiffsSmallTable(seatDiffs: SeatDiffs, modifier: Modifier = Modifier) {
    val first = seatDiffs.pointsToBeFirst ?: return
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            DiffsSmallCell("")
            DiffsSmallCell(stringResource(Res.string.self_abr))
            DiffsSmallCell(stringResource(Res.string.direct_abr))
            DiffsSmallCell(stringResource(Res.string.indirect_abr))
        }
        DiffsSmallRow(stringResource(Res.string._1st), first)
        seatDiffs.pointsToBeSecond?.let { DiffsSmallRow(stringResource(Res.string._2nd), it) }
        seatDiffs.pointsToBeThird?.let { DiffsSmallRow(stringResource(Res.string._3rd), it) }
    }
}

@Composable
private fun DiffsSmallRow(title: String, diff: PointsDiff) {
    Row {
        DiffsSmallCell(title)
        DiffsSmallCell(diff.bySelfPick.toString())
        DiffsSmallCell(diff.byDirectHu.toString())
        DiffsSmallCell(diff.byIndirectHu.toString())
    }
}

/** MS2 "diffsGameCell" style. */
@Composable
private fun DiffsSmallCell(text: String) {
    Text(
        text = text,
        color = MmColors.GrayDiffs,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.width(MmDimens.StandardAndHalfMargin),
    )
}

/** MS2 "custom_diffs_big", shown in the hand actions dialog. */
@Composable
fun DiffsBigTable(seatDiffs: SeatDiffs, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DiffsBigTitleCell(stringResource(Res.string.to_be_by), bold = false)
            DiffsBigTitleCell(stringResource(Res.string.self_pick))
            DiffsBigTitleCell(stringResource(Res.string.direct))
            DiffsBigTitleCell(stringResource(Res.string.indirect))
        }
        DiffsBigRow(stringResource(Res.string._1st), seatDiffs.pointsToBeFirst)
        DiffsBigRow(stringResource(Res.string._2nd), seatDiffs.pointsToBeSecond)
        DiffsBigRow(stringResource(Res.string._3rd), seatDiffs.pointsToBeThird)
    }
}

@Composable
private fun DiffsBigRow(title: String, diff: PointsDiff?) {
    Row {
        DiffsBigCell(title)
        DiffsBigCell(diff?.bySelfPick.toStringOrHyphen())
        DiffsBigCell(diff?.byDirectHu.toStringOrHyphen())
        DiffsBigCell(diff?.byIndirectHu.toStringOrHyphen())
    }
}

@Composable
private fun RowScope.DiffsBigTitleCell(text: String, bold: Boolean = true) {
    Text(
        text = text,
        color = MmColors.GrayMM,
        fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f).padding(MmDimens.QuarterMargin),
    )
}

@Composable
private fun RowScope.DiffsBigCell(text: String) {
    Text(
        text = text,
        color = MmColors.GrayMM,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.weight(1f).padding(MmDimens.QuarterMargin),
    )
}

/** MS2 "CustomNumPad": display (max 4 digits) and keys. */
@Composable
fun NumPad(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value.ifEmpty { "-" },
                color = if (value.isEmpty()) MmColors.Gray else MmColors.GrayMM,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = MmDimens.HalfMargin).testTag("tvCustomNumPadPoints"),
            )
            if (isError) {
                Text("!", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    modifier = Modifier.size(20.dp).clip(RoundedCornerShape(10.dp)).background(MmColors.Red))
            }
        }
        fun add(digit: String) {
            if (value.length < 4) onValueChange(value + digit)
        }
        listOf(listOf("7", "8", "9"), listOf("4", "5", "6"), listOf("1", "2", "3")).forEach { row ->
            Row {
                row.forEach { digit -> NumPadButton(tag = "btCustomNumPad$digit", onClick = { add(digit) }) { Text(digit, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) } }
            }
        }
        Row {
            NumPadButton(tag = "btCustomNumPadBackspace", onClick = { if (value.isNotEmpty()) onValueChange(value.dropLast(1)) }) {
                Icon(painterResource(Res.drawable.ic_backspace_white_24dp), contentDescription = stringResource(Res.string.backspace), tint = Color.White)
            }
            NumPadButton(tag = "btCustomNumPad0", onClick = { add("0") }) { Text("0", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(64.dp))
        }
    }
}

/** MS2 "ButtonNumPad" style: 64x48dp green buttons. */
@Composable
private fun NumPadButton(tag: String, onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = MmColors.Primary, contentColor = Color.White),
        // MS2 ButtonNumPad: a 64x48dp Button whose background has 4dp/6dp insets
        modifier = Modifier.width(64.dp).height(48.dp).padding(horizontal = 4.dp, vertical = 6.dp).testTag(tag),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        content = content,
    )
}

/** MS2 "CustomDiscarderSelector": the three possible discarders, tap again to unselect (self pick). */
@Composable
fun DiscarderSelector(
    seats: List<Pair<TableWinds, String>>,
    selected: TableWinds,
    onSelect: (TableWinds) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth()) {
        seats.forEachIndexed { index, (wind, name) ->
            val isSelected = wind == selected
            val color = if (isSelected) MmColors.Accent else MmColors.GrayMM
            Column(
                modifier = Modifier
                    .weight(1f)
                    .testTag("iDiscarderSelectorPlayer${index + 1}Container")
                    .clip(RoundedCornerShape(MmDimens.HalfMargin))
                    .background(if (isSelected) MmColors.Gray else Color.Transparent)
                    .clickable { onSelect(if (isSelected) TableWinds.NONE else wind) }
                    .padding(MmDimens.HalfMargin),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WindIcon(wind, color, size = MmDimens.StandardAndHalfMargin)
                Text(name, color = color, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
    }
}

