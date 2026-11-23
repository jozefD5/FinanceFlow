package com.jd.financeflow.features.home.presentation

import androidx.compose.runtime.Stable

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.core.domain.models.finance.Finance

/**
 * Represents the UI state of the home screen.
 *
 * @property isLoading Whether data is currently loading.
 * @property error An optional error message or resource to display ([UiText]).
 * @property currencyCode The ISO currency code used for formatting.
 * @property balance The net financial balance.
 * @property income The total income amount.
 * @property expense The total expense amount.
 * @property recentTransactions The list of recent financial transactions.
 */
@Stable
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
