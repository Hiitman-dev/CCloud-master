package com.pira.ccloud.util

/**
 * A generic result type that represents either success or failure.
 * Use this as the return type for all repository methods.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: ApiException) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun exceptionOrNull(): ApiException? = when (this) {
        is Error -> exception
        else -> null
    }

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(exception: ApiException): Result<T> = Error(exception)
        fun <T> loading(): Result<T> = Loading
    }
}
