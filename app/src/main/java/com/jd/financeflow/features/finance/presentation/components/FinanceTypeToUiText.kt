package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.TransactionType

/**
 * Converts a [TransactionType] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [TransactionType] value.
 */
fun TransactionType.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceID()
    )
}

/**
 * Converts a [TransactionType] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [TransactionType] value.
 */
fun TransactionType.toStringResourceID(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.finance_type_income
        TransactionType.EXPENSE -> R.string.finance_type_expense
    }
}

