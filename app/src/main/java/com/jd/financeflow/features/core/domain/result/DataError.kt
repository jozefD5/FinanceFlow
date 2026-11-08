package com.jd.financeflow.features.core.domain.result

/**
 * Data error implementation of [Error] interface.
 */
sealed interface DataError : Error {
    /**
     * Local storage or database related errors.
     */
    enum class Local : DataError {
        /** Invalid argument provided. */
        INVALID_ARGUMENT,
        /** Invalid request made. */
        INVALID_REQUEST,
        /** Serialization failure. */
        SERIALIZATION,
        /** Parsing failure. */
        PARSING,
        /** Request timed out. */
        REQUEST_TIMEOUT,
        /** Storage is full. */
        STORAGE_FULL,
        /** Storage is unavailable. */
        STORAGE_UNAVAILABLE,
        /** Unknown error occurred. */
        UNKNOWN
    }
}