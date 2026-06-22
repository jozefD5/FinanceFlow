package com.jd.financeflow.features.finance.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.finance.domain.models.FinanceInterval
import com.jd.financeflow.features.core.presentation.UiText

/**
 * Converts a [FinanceInterval] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [FinanceInterval] value.
 */
fun FinanceInterval.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceId()
    )
}

/**
 * Converts a [FinanceInterval] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceInterval] value.
 */
private fun FinanceInterval.toStringResourceId(): Int {
    return when (this) {
        FinanceInterval.OneTime -> R.string.finance_interval_one_time
        FinanceInterval.Daily -> R.string.finance_interval_daily
        FinanceInterval.Weekly -> R.string.finance_interval_weekly
        FinanceInterval.Monthly -> R.string.finance_interval_monthly
        FinanceInterval.Quarterly -> R.string.finance_interval_quarterly
        FinanceInterval.SemiAnnually -> R.string.finance_interval_half_yearly
        FinanceInterval.Annually -> R.string.finance_interval_yearly
    }
}