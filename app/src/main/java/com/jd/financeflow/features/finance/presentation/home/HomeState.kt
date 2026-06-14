package com.jd.financeflow.features.finance.presentation.home

import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.Finance

/**
 * Data class representing the state of the home screen.
 */
data class HomeState(
    // General
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    // Balance card
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,

    // Recent transactions
    val recentTransactions: List<Finance> = emptyList(),

    // Income category preview
    val incomeCategoriesPreview: List<Pair<FinanceCategory, Double>> = emptyList(),
    val expenseCategoriesPreview: List<Pair<FinanceCategory, Double>> = emptyList()
)
