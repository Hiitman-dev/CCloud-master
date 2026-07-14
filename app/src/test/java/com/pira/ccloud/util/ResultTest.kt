package com.pira.ccloud.util

import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `Success result contains data`() {
        val result = Result.success("test data")
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
        assertEquals("test data", result.getOrNull())
    }

    @Test
    fun `Error result contains exception`() {
        val exception = ApiException.NetworkError(null)
        val result = Result.error<String>(exception)
        assertFalse(result.isSuccess)
        assertTrue(result.isError)
        assertFalse(result.isLoading)
        assertNull(result.getOrNull())
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `Loading result has no data`() {
        val result = Result.loading<String>()
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
        assertTrue(result.isLoading)
        assertNull(result.getOrNull())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun `map transforms success data`() {
        val result = Result.success(5)
        val mapped = result.map { it * 2 }
        assertTrue(mapped.isSuccess)
        assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun `map preserves error`() {
        val exception = ApiException.NetworkError(null)
        val result = Result.error<Int>(exception)
        val mapped = result.map { it * 2 }
        assertTrue(mapped.isError)
        assertEquals(exception, mapped.exceptionOrNull())
    }

    @Test
    fun `map preserves loading`() {
        val result = Result.loading<Int>()
        val mapped = result.map { it * 2 }
        assertTrue(mapped.isLoading)
    }
}
