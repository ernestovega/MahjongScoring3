package ui.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Immutable
data class SmallSeatsState(
    val eastSeat: SeatState = SeatState(),
    val southSeat: SeatState = SeatState(),
    val westSeat: SeatState = SeatState(),
    val northSeat: SeatState = SeatState(),
)

@Composable
fun SmallSeats(
    state: SmallSeatsState,
    onSeatClick: ((SeatState) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val centralBoxSize = 32.dp
        val horizontalSeparation = centralBoxSize * 2
        val verticalSeparation = centralBoxSize * 1.75f

        //CENTRAL BOX
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(centralBoxSize),
        )
        // BOTTOM - EAST
        SmallSeat(
            state = state.eastSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = verticalSeparation),
            onClick = onSeatClick,
        )
        // RIGHT - SOUTH
        SmallSeat(
            state = state.southSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = horizontalSeparation),
            onClick = onSeatClick,
        )
        // TOP - WEST
        SmallSeat(
            state = state.westSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -verticalSeparation),
            onClick = onSeatClick,
        )
        // LEFT - NORTH
        SmallSeat(
            state = state.northSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -horizontalSeparation),
            onClick = onSeatClick,
        )
    }
}
