package com.jd.financeflow.features.finance.presentation.home

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Data class representing the state of the home screen.
 */
data class HomeState(
    // General
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    // Balance card
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,

    // Recent transactions
    val recentTransactions: List<Finance> = emptyList(),
)
