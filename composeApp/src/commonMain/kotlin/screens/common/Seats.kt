package screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Seats(
    seatsState: List<Pair<String, Int>> = listOf(
        "Eto" to 100,
        "Cris" to 100,
        "Maricarmen" to -100,
        "Covadonga" to -100
    ),
    modifier: Modifier = Modifier,
) {
    val spaceBetweenSeats = 40.dp
    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spaceBetweenSeats),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Seat(seatState = seatsState[0])
            Seat(seatState = seatsState[1])
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenSeats),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Seat(seatState = seatsState[2])
            Seat(seatState = seatsState[3])
        }
    }
}
