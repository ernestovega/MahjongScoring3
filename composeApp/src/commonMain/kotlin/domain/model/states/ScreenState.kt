package domain.model.states

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


//package domain.model.states
//
//sealed interface ScreenState {
//
//    data class Error(val error: Throwable): ScreenState
//
//    data object Loading: ScreenState
//
//    data class Success<T>(val data: T): ScreenState
//}
//
//val ScreenState.error get() = (this as? ScreenState.Error)?.error
//val ScreenState.data get() = (this as? ScreenState.Success<*>)?.data
