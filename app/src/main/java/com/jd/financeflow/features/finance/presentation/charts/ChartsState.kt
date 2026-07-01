package com.jd.financeflow.features.finance.presentation.charts

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Finance charts view state
 */
data class ChartsState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val selectedIntervalIndex: Int = 0,
)
