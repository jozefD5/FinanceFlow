package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

data class FinanceListFilterState(
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val selectedSortIndex: Int = 0,
    val selectedTypeIndex: Int = 0,
    val enableCategories: Boolean = false,

    // Selected options for each category, state of chack boxes.
    val selectedIncomeCategories: List<Boolean> = IncomeCategory.entries.map { true },
    val selectedExpenseCategories: List<Boolean> = ExpenseCategory.entries.map { true },

    // Amount range
    val enableAmountRange: Boolean = false,
    val minimumAmount: Float = 0.0f,
    val maximumAmount: Float = 100_000_00.0f,

    // Mini and max allowed amount range
    val selectedAmountRangeMini: Float = 0.0f,
    val selectedAmountRangeMax: Float = 100_000_00.0f,
)
