package com.pira.ccloud.ui

import com.pira.ccloud.util.ApiException

/**
 * Generic UI state sealed class for all screens.
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: ApiException) : UiState<Nothing>() {
        val message: String get() = exception.message ?: "An unexpected error occurred"
    }

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    val isIdle: Boolean get() = this is Idle

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}
