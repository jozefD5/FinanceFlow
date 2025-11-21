package com.jd.financeflow.features.core.presentation

import com.jd.financeflow.R
import com.jd.financeflow.features.core.domain.DataError

/**
 * Converts a [DataError] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [DataError] value.
 */
fun DataError.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceId()
    )
}

/**
 * Converts a [DataError] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [DataError] value.
 */
fun DataError.toStringResourceId(): Int {
    return when (this) {
        DataError.Local.INVALID_ARGUMENT -> R.string.data_error_invalid_argument
        DataError.Local.INVALID_REQUEST -> R.string.data_error_invalid_request
        DataError.Local.SERIALIZATION -> R.string.data_error_serialization
        DataError.Local.PARSING -> R.string.data_error_parsing
        DataError.Local.REQUEST_TIMEOUT -> R.string.data_error_request_timeout
        DataError.Local.STORAGE_FULL -> R.string.data_error_storage_full
        DataError.Local.STORAGE_UNAVAILABLE -> R.string.data_error_storage_unavailable
        DataError.Local.UNKNOWN -> R.string.data_error_not_unknown
    }
}