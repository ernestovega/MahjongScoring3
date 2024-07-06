import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import screens.common.ui.BaseViewModel

class AppViewModel : BaseViewModel() {

    private val _isDarkModeFlow = MutableStateFlow(false)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow

    fun changeColorMode() = _isDarkModeFlow.update { value -> value.not() }
}