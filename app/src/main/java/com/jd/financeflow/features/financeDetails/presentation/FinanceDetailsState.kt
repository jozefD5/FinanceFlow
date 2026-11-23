package com.jd.financeflow.features.financeDetails.presentation

import androidx.compose.runtime.Stable

import com.jd.financeflow.features.core.presentation.UiText

/**
 * Represents the UI state of the finance details screen.
 *
 * @property isLoading Whether data is currently loading or being saved.
 * @property error An optional error message or resource to display ([UiText]).
 * @property currencyCode The ISO currency code used for the amount prefix.
 * @property invalidTitle Whether the title failed validation on save.
 * @property invalidAmount Whether the amount failed validation on save.
 * @property title The transaction title.
 * @property description The optional transaction description.
 * @property amount The transaction amount as entered and formatted for display.
 * @property startDate The transaction start date in epoch milliseconds.
 * @property financeIntervalIndex Index of the selected interval in `FinanceInterval.entries`.
 * @property financeTypeIndex Index of the selected type in `FinanceType.entries`.
 * @property financeCategoryIndex Index of the selected category in `IncomeCategory.entries` or
 * `ExpenseCategory.entries`, depending on [financeTypeIndex].
 */
@Stable
data class FinanceDetailsState(
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val invalidTitle: Boolean = false,
    val invalidAmount: Boolean = false,

    val title: String = "",
    val description: String = "",
    val amount: String = "",

    val enableEndDate: Boolean = false,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),

    val financeIntervalIndex: Int = 0,
    val financeTypeIndex: Int = 0,
    val financeCategoryIndex: Int = 0,
)