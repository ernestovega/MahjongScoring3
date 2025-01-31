package screens.common.model.states

sealed interface ScreenState<T> {

    val data: T

    data class Error<T>(
        val error: Throwable,
        override val data: T,
    ): ScreenState<T>

    data class Loading<T>(
        override val data: T,
    ): ScreenState<T>

    data class Success<T>(
        override val data: T,
    ): ScreenState<T>
}

val ScreenState<*>.error get() = (this as? ScreenState.Error<*>)?.error
