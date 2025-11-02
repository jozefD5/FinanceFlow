package com.jd.financeflow.features.core.domain

/**
 * Data error implementation of [Error] interface
 */
sealed interface DataError: Error {
    enum class Local: DataError {
        INVALID_ARGUMENT,
        INVALID_REQUEST,
        SERIALIZATION,
        PARSING,
        REQUEST_TIMEOUT,
        DISK_FULL,
        UNKNOWN
    }
}