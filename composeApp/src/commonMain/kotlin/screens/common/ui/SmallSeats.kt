package screens.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SmallSeatsState(
    val eastSeat: SmallSeatState = SmallSeatState(),
    val southSeat: SmallSeatState = SmallSeatState(),
    val westSeat: SmallSeatState = SmallSeatState(),
    val northSeat: SmallSeatState = SmallSeatState(),
)

@Composable
fun SmallSeats(
    state: SmallSeatsState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val centralBoxSize = 32.dp * 2

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(centralBoxSize / 2),
        )

        // BOTTOM - EAST
        SmallSeat(
            state = state.eastSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = centralBoxSize),
        )

        // RIGHT - SOUTH
        SmallSeat(
            state = state.southSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = centralBoxSize),
        )

        // TOP - WEST
        SmallSeat(
            state = state.westSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -centralBoxSize),
        )

        // LEFT - NORTH
        SmallSeat(
            state = state.northSeat,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -centralBoxSize),
        )
    }
}
