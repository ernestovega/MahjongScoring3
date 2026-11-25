package com.etologic.mahjongscoring3.ui.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel : ViewModel() {
    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error.asStateFlow()

    fun showError(throwable: Throwable) {
        throwable.printStackTrace()
        _error.value = throwable
    }

    fun dismissError() {
        _error.value = null
    }
}
