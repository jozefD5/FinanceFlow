package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

data class FinanceListFilterState(
    val error: UiText? = null,
    val currencyCode: String = "EUR",

    val selectedSortIndex: Int = 0,
    val selectedTypeIndex: Int = 0,
    val selectedType: FinanceType? = null,
    val enableCategories: Boolean = false,

    // Available income options to be presented in the UI
    val categoryIncomeOptions: List<FinanceCategory> = IncomeCategory.entries.map {
        FinanceCategory.Income(it)
    },
    val categoryExpenseOptions: List<FinanceCategory> = ExpenseCategory.entries.map {
        FinanceCategory.Expense(it)
    },

    // Selected options for each category, state of chack boxes.
    val categoryIncomeSelectedOptions: List<Boolean> = IncomeCategory.entries.map { true },
    val categoryExpenseSelectedOptions: List<Boolean> = ExpenseCategory.entries.map { true },

    val enableAmountRange: Boolean = false,
    val minimumAmount: Float = 0.0f,
    val maximumAmount: Float = 100_000_00.0f,

    val selectedAmountRangeMini: Float = 0.0f,
    val selectedAmountRangeMax: Float = 100_000_00.0f,
)
