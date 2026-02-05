package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.SortOrder

/**
 * Converts a [SortOrder] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [SortOrder] value.
 */
fun SortOrder.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceId()
    )
}

/**
 * Converts a [SortOrder] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [SortOrder] value.
 */
fun SortOrder.toStringResourceId(): Int {
    return when (this) {
        SortOrder.ASCENDING -> R.string.sort_order_ascending
        SortOrder.DESCENDING -> R.string.sort_order_descending
        SortOrder.NEWEST_FIRST -> R.string.sort_order_newest_first
        SortOrder.OLDEST_FIRST -> R.string.sort_order_oldest_first
    }
}