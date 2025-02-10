package ui.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Immutable
data class NameTextFieldState(
    val text: String,
    val hint: StringResource,
)

private const val MAX_NAME_LENGTH = 12

@Composable
fun NameTextField(
    state: NameTextFieldState,
    onNameChanged: (String) -> Unit,
    focusRequester: FocusRequester? = null,
) {
    var text by remember { mutableStateOf(state.text) }

    Row(
        modifier = Modifier.padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(8.dp))

        MMTextField(
            modifier = Modifier
                .fillMaxWidth()
                .applyIfNotNull(focusRequester) { focusRequester(it) },
            value = text,
            onValueChange = { newText ->
                if (newText.length <= MAX_NAME_LENGTH) {
                    text = newText
                    onNameChanged(newText)
                }
            },
            placeholder = { Text(stringResource(state.hint)) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colors.secondary,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
            ),
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
