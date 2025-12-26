package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.FinanceType

/**
 * Converts a [FinanceType] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [FinanceType] value.
 */
fun FinanceType.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceID()
    )
}

/**
 * Converts a [FinanceType] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceType] value.
 */
fun FinanceType.toStringResourceID(): Int {
    return when (this) {
        FinanceType.INCOME -> R.string.finance_type_income
        FinanceType.EXPENSE -> R.string.finance_type_expense
    }
}

