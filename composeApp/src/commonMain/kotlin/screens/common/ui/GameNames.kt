package screens.common.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

data class GameNames(
    val gameName: String = "",
    val nameP1: String = "",
    val nameP2: String = "",
    val nameP3: String = "",
    val nameP4: String = "",
)

@Immutable
data class NameTextFieldState(
    val text: String,
    val hint: StringResource,
)

@Composable
fun NameTextField(
    state: NameTextFieldState,
    focusRequester: FocusRequester? = null,
    onNameChanged: (String) -> Unit,
) {
    var shouldSelectAll by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(8.dp))

        MMTextField(
            value = state.text,
            shouldSelectAll = shouldSelectAll,
            onValueChange = { onNameChanged(it) },
            placeholder = { Text(stringResource(state.hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .applyIfNotNull(focusRequester) { focusRequester(it) }
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        shouldSelectAll = true
                    }
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
            ),
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
