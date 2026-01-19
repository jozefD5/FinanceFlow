package com.jd.financeflow.features.finance.presentation.financeListFilter

import com.jd.financeflow.features.core.presentation.UiText
import com.jd.financeflow.features.finance.domain.models.ExpenseCategory
import com.jd.financeflow.features.finance.domain.models.FinanceCategory
import com.jd.financeflow.features.finance.domain.models.FinanceType
import com.jd.financeflow.features.finance.domain.models.IncomeCategory

data class FinanceListFilterState(
    val error: UiText? = null,

    val selectedTypeIndex: Int = 0,
    val selectedType: FinanceType? = null,

    val enableCategories: Boolean = false,
    val categoryIncomeOptions: List<FinanceCategory> = IncomeCategory.entries.map {
        FinanceCategory.Income(it)
    },
    val categoryExpenseOptions: List<FinanceCategory> = ExpenseCategory.entries.map {
        FinanceCategory.Expense(it)
    },
    val categoryIncomeSelectedOptions: List<Boolean> = IncomeCategory.entries.map { true },
    val categoryExpenseSelectedOptions: List<Boolean> = ExpenseCategory.entries.map { true },

    val enableAmountRange: Boolean = false,
    val selectedAmountRangeMini: Double? = null,
    val selectedAmountRangeMax: Double? = null
)
