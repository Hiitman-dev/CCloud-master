package com.pira.ccloud.util

import org.junit.Assert.*
import org.junit.Test

class ApiExceptionTest {

    @Test
    fun `NetworkError has correct message`() {
        val exception = ApiException.NetworkError(null)
        assertEquals("Network error. Please check your connection.", exception.message)
    }

    @Test
    fun `ServerError has correct message with code`() {
        val exception = ApiException.ServerError(404)
        assertEquals("Server error (404). Please try again later.", exception.message)
    }

    @Test
    fun `ParseError has correct message`() {
        val exception = ApiException.ParseError(null)
        assertEquals("Failed to parse response data.", exception.message)
    }

    @Test
    fun `NotFound has correct message`() {
        val exception = ApiException.NotFound()
        assertEquals("Requested content not found.", exception.message)
    }

    @Test
    fun `Unauthorized has correct message`() {
        val exception = ApiException.Unauthorized()
        assertEquals("Authentication failed.", exception.message)
    }

    @Test
    fun `UnknownError has correct message`() {
        val exception = ApiException.UnknownError()
        assertEquals("An unexpected error occurred.", exception.message)
    }

    @Test
    fun `fromException converts UnknownHostException to NetworkError`() {
        val ioException = java.net.UnknownHostException("No host")
        val result = ApiException.fromException(ioException)
        assertTrue(result is ApiException.NetworkError)
    }

    @Test
    fun `fromException converts SocketTimeoutException to NetworkError`() {
        val timeoutException = java.net.SocketTimeoutException("Timeout")
        val result = ApiException.fromException(timeoutException)
        assertTrue(result is ApiException.NetworkError)
    }

    @Test
    fun `fromException converts IOException to NetworkError`() {
        val ioException = java.io.IOException("IO error")
        val result = ApiException.fromException(ioException)
        assertTrue(result is ApiException.NetworkError)
    }

    @Test
    fun `fromException preserves ApiException`() {
        val original = ApiException.ServerError(500)
        val result = ApiException.fromException(original)
        assertSame(original, result)
    }
}
