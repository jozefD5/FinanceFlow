package com.jd.financeflow.features.core.presentation.components

import com.jd.financeflow.R
import com.jd.financeflow.features.core.domain.finance.FinanceCategory
import com.jd.financeflow.features.core.presentation.UiText

/**
 * Converts a [FinanceCategory] to a [UiText] object.
 *
 * @return The [UiText] object corresponding to the [FinanceCategory] value.
 */
fun FinanceCategory.toUiText(): UiText {
    return UiText.StringResource(
        id = this.toStringResourceId()
    )
}

/**
 * Converts a [FinanceCategory] to a string resource ID.
 *
 * @return The string resource ID corresponding to the [FinanceCategory] value.
 */
private fun FinanceCategory.toStringResourceId(): Int {
    return when (this) {
        is FinanceCategory.Income -> {
            when (this.category) {
                FinanceCategory.IncomeCategory.Salary -> R.string.finance_category_salary
                FinanceCategory.IncomeCategory.Bonus -> R.string.finance_category_bonus
                FinanceCategory.IncomeCategory.Commissions -> R.string.finance_category_commissions
                FinanceCategory.IncomeCategory.Benefits -> R.string.finance_category_benefits
                FinanceCategory.IncomeCategory.Investments -> R.string.finance_category_investments
                FinanceCategory.IncomeCategory.Other -> R.string.finance_category_other
            }
        }

        is FinanceCategory.Expense -> {
            when (this.category) {
                FinanceCategory.ExpenseCategory.Utility -> R.string.finance_category_utility
                FinanceCategory.ExpenseCategory.Housing -> R.string.finance_category_housing
                FinanceCategory.ExpenseCategory.Transport -> R.string.finance_category_transport
                FinanceCategory.ExpenseCategory.Food -> R.string.finance_category_food
                FinanceCategory.ExpenseCategory.Entertainment -> R.string.finance_category_entertainment
                FinanceCategory.ExpenseCategory.Health -> R.string.finance_category_health
                FinanceCategory.ExpenseCategory.Education -> R.string.finance_category_education
                FinanceCategory.ExpenseCategory.Other -> R.string.finance_category_other
            }
        }
    }
}