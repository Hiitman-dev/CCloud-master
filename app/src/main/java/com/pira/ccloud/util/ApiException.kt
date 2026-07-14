package com.pira.ccloud.util

/**
 * Typed exceptions for API and data layer errors.
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    data class NetworkError(override val cause: Throwable?) : ApiException("Network error. Please check your connection.", cause)
    data class ServerError(val code: Int, override val cause: Throwable? = null) : ApiException("Server error ($code). Please try again later.", cause)
    data class ParseError(override val cause: Throwable?) : ApiException("Failed to parse response data.", cause)
    data class NotFound(override val cause: Throwable? = null) : ApiException("Requested content not found.", cause)
    data class Unauthorized(override val cause: Throwable? = null) : ApiException("Authentication failed.", cause)
    data class UnknownError(override val cause: Throwable? = null) : ApiException("An unexpected error occurred.", cause)

    companion object {
        fun fromException(e: Exception): ApiException = when (e) {
            is ApiException -> e
            is java.net.UnknownHostException -> NetworkError(e)
            is java.net.SocketTimeoutException -> NetworkError(e)
            is java.io.IOException -> NetworkError(e)
            is kotlinx.serialization.SerializationException -> ParseError(e)
            is org.json.JSONException -> ParseError(e)
            else -> UnknownError(e)
        }
    }
}
