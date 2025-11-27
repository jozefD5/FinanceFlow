package com.jd.financeflow.features.core.domain

/**
 * A sealed interface representing a result of an operation that can either be a [Success] or an [Error].
 *
 * @param D The type of the successful data.
 * @param E The type of the error, which must be a subtype of [com.jd.financeflow.features.core.domain.Error].
 */
sealed interface Result<out D, out E : Error> {
    /**
     * Represents a successful result.
     *
     * @param D The type of the successful data.
     * @property data The successful data.
     */
    data class Success<out D>(val data: D) : Result<D, Nothing>

    /**
     * Represents a failed result.
     *
     * @param E The type of the error, which must be a subtype of [com.jd.financeflow.features.core.domain.Error].
     * @property error The error that occurred.
     */
    data class Error<out E : com.jd.financeflow.features.core.domain.Error>(val error: E) :
        Result<Nothing, E>
}

/**
 * Maps a `Result<T, E>` to `Result<R, E>` by applying a function to a success value, leaving an error untouched.
 *
 * @param T The type of the successful data.
 * @param E The type of the error.
 * @param R The type of the new successful data.
 * @param map The function to apply to the successful data.
 * @return A new `Result` with the mapped data if it was a `Success`, otherwise the original `Error`.
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

/**
 * Converts a `Result<T, E>` to an `EmptyResult<E>`, preserving the error if it exists, or returning a `Success` with `Unit`.
 *
 * @param T The type of the successful data.
 * @param E The type of the error.
 * @return An `EmptyResult<E>`.
 */
fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

/**
 * Performs the given [action] on the encapsulated data if this instance represents [Success].
 * Returns the original `Result` unchanged.
 *
 * @param T The type of the successful data.
 * @param E The type of the error.
 * @param action The action to perform on the successful data.
 * @return The original `Result`.
 */
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

/**
 * Performs the given [action] on the encapsulated error if this instance represents [Error].
 * Returns the original `Result` unchanged.
 *
 * @param T The type of the successful data.
 * @param E The type of the error.
 * @param action The action to perform on the error.
 * @return The original `Result`.
 */
inline fun <T, E : Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error)
            this
        }

        is Result.Success -> this
    }
}

/**
 * A typealias for a `Result` that has `Unit` as its success type.
 *
 * @param E The type of the error.
 */
typealias EmptyResult<E> = Result<Unit, E>
