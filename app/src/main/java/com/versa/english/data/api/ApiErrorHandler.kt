package com.versa.english.data.api

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ApiErrorHandler {
    private const val MAX_RETRIES = 3
    private const val INITIAL_RETRY_DELAY = 1000L // 1 second

    suspend fun <T> withRetry(block: suspend () -> T): T {
        var currentDelay = INITIAL_RETRY_DELAY
        var retryCount = 0

        while (true) {
            try {
                return block()
            } catch (e: Exception) {
                if (retryCount >= MAX_RETRIES) {
                    throw e
                }

                when (e) {
                    is HttpException -> {
                        when (e.code()) {
                            429 -> {
                                // Rate limit exceeded
                                retryCount++
                                delay(currentDelay)
                                currentDelay *= 2 // Exponential backoff
                            }
                            else -> throw e
                        }
                    }
                    is SocketTimeoutException, is IOException -> {
                        // Network issues
                        retryCount++
                        delay(currentDelay)
                        currentDelay *= 2
                    }
                    else -> throw e
                }
            }
        }
    }
} 