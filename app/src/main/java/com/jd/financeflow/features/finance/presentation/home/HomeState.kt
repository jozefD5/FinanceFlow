package com.jd.financeflow.features.finance.presentation.home

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Data class representing the state of the home screen.
 */
data class HomeState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
