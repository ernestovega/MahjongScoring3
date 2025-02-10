package ui.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumPad(
    title: String,
    onNumberChanged: (Int) -> Unit,
) {
    var penaltyPoints by remember { mutableStateOf("-") }
    val onNumberPressed = { number: String ->
        if ((penaltyPoints != "-" || number != "0") && penaltyPoints.length < 4) {
            penaltyPoints = if (penaltyPoints == "-") {
                number
            } else {
                penaltyPoints.plus(number)
            }
            onNumberChanged(penaltyPoints.toInt())
        }
    }
    val onBackspacePressed = { _:String ->
        penaltyPoints = if (penaltyPoints.length == 1) {
            "-"
        } else {
            penaltyPoints.dropLast(1)
        }
        onNumberChanged(if (penaltyPoints == "-") 0 else penaltyPoints.toInt())
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "$title:",
            fontSize = 20.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = penaltyPoints,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row {
            NumPadButton("9", onNumberPressed)
            NumPadButton("8", onNumberPressed)
            NumPadButton("7", onNumberPressed)
        }
        Row {
            NumPadButton("6", onNumberPressed)
            NumPadButton("5", onNumberPressed)
            NumPadButton("4", onNumberPressed)
        }
        Row {
            NumPadButton("3", onNumberPressed)
            NumPadButton("2", onNumberPressed)
            NumPadButton("1", onNumberPressed)
        }
        Row {
            NumPadButton("←", onBackspacePressed)
            NumPadButton("0") { onNumberPressed("0") }
            NumPadGhostButton()
        }
    }
}

@Composable
private fun NumPadButton(text: String, onClick: (String) -> Unit) {
    Button(
        modifier = Modifier
            .width(64.dp)
            .height(48.dp)
            .padding(4.dp),
        onClick = { onClick(text) },
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
        )
    }
}

@Composable
private fun NumPadGhostButton() {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(48.dp)
            .padding(4.dp),
    )
}