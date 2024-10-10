package com.example.myapplication

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel {
    private val _uiState = MutableStateFlow(SliderContent(0))
    val uiState: StateFlow<SliderContent> = _uiState.asStateFlow()

    @OptIn(DelicateCoroutinesApi::class)
    fun updateSliderValue(value: Int) {
        GlobalScope.launch {
            // delay(500)
            _uiState
                .update {
                    it.copy(value = value)
                }
        }
    }
}

data class SliderContent(
    val value: Int,
)
