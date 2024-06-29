package screens.common


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SmallSeats(
    seatsState: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val centralBoxSize = 32.dp * 2

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(centralBoxSize / 2),
        )

        SmallSeat(
            seatState = seatsState[0],
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -centralBoxSize),
        )

        SmallSeat(
            seatState = seatsState[1],
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = centralBoxSize),
        )

        SmallSeat(
            seatState = seatsState[2],
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -centralBoxSize),
        )

        SmallSeat(
            seatState = seatsState[3],
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = centralBoxSize),
        )
    }
}
