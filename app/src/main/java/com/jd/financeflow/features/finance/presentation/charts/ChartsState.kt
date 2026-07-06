package com.jd.financeflow.features.finance.presentation.charts

import androidx.compose.runtime.Stable

import com.jd.financeflow.features.core.presentation.UiText

@Stable
data class ChartsState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val selectedIntervalIndex: Int = 0,
)
