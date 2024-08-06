package screens.common.ui

import androidx.compose.ui.Modifier

fun Modifier.applyIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier =
    if (condition) {
        this then (modifier(Modifier))
    } else {
        this
    }

fun <T> Modifier.applyIfNotNull(
    nullableObject: T?,
    modifier: Modifier.(notNullObject: T) -> Modifier,
): Modifier =
    if (nullableObject != null) {
        this then (modifier(nullableObject))
    } else {
        this
    }