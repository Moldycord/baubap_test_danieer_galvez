package com.baubaptest.core.model


sealed class CustomResult<out T> {
    data class Success<out T>(val data: T) : CustomResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : CustomResult<Nothing>()
    object Loading : CustomResult<Nothing>()
}

fun <T> CustomResult<T>.asUiState(): UiState<T> = when (this) {
    is CustomResult.Success -> UiState.Success(data)
    is CustomResult.Error -> UiState.Error(message, throwable)
    is CustomResult.Loading -> UiState.Loading
}